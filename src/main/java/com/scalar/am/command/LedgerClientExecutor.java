package com.scalar.am.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.client.config.ClientConfig;
import com.scalar.client.service.ClientModule;
import com.scalar.client.service.ClientService;
import com.scalar.client.service.StatusCode;
import com.scalar.rpc.ledger.ContractExecutionResponse;
import com.scalar.rpc.ledger.LedgerValidationResponse;
import java.io.File;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.DatatypeConverter;

/** Class used to execute method on a {@link com.scalar.ledger.ledger.Ledger} */
abstract class LedgerClientExecutor {

  private ClientConfig loadClientConfig() throws Exception {
    File file = new File(System.getProperty("user.dir") + File.separator + "client.properties");
    return new ClientConfig(file);
  }

  /**
   * Execute the contract on the ledger. <br>
   * First, it will lookup on the configuration which id has been used to register the contract and
   * then execute it. <br>
   * The output will be printed to the standard output.
   *
   * @param contractName the contract name
   * @param contractParameter the parameter that will be passed to the contract
   */
  protected void executeContract(String contractName, JsonObject contractParameter) {
    try {
      ClientConfig config = loadClientConfig();
      String contractId = contractName + "_" + config.getCertHolderId();

      LedgerExecutorFunction f =
          (clientService) -> {
            JsonObject object =
                (contractParameter != null)
                    ? contractParameter
                    : Json.createObjectBuilder().build();
            ContractExecutionResponse response = clientService.executeContract(contractId, object);
            if (response.getStatus() != StatusCode.OK.get()) {
              System.err.println("Error during contract execution");
              System.err.println("Status code: " + response.getStatus());
              System.err.println("Message: " + response.getMessage());
              return;
            }
            JsonReader reader = Json.createReader(new StringReader(response.getResult()));
            prettyPrintJson((JsonObject) reader.read());
          };

      this.executeOnLedger(f);
    } catch (Exception e) {
      e.printStackTrace();
      ;
    }
  }

  protected void validateAsset(String id) {
    try {
      LedgerExecutorFunction f =
          (clientService) -> {
            LedgerValidationResponse response = clientService.validateLedger(id);
            if (response.getStatus() != StatusCode.OK.get()) {
              System.err.println("Error during asset validate");
              System.err.println("Status code: " + response.getStatus());
              System.err.println("Message: " + response.getMessage());
              return;
            }
            System.out.println("Asset " + id + " is untampered");
          };

      executeOnLedger(f);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Execute the function on the ledger passed on the configuration
   *
   * @param f the function to execute
   */
  protected final void executeOnLedger(LedgerExecutorFunction f) throws Exception {
    ClientConfig config = loadClientConfig();
    Injector injector = Guice.createInjector(new ClientModule(config));
    try (ClientService clientService = injector.getInstance(ClientService.class)) {
      f.execute(clientService);
    }
  }

  /**
   * Pretty print the json to the standard output
   *
   * @param json the json to print
   */
  protected void prettyPrintJson(JsonObject jsonObject) {
    if (jsonObject != null) {
      System.out.println("[Return]");
      Map<String, Object> config = new HashMap<>(1);
      config.put(JsonGenerator.PRETTY_PRINTING, true);
      JsonWriterFactory factory = Json.createWriterFactory(config);
      JsonWriter writer = factory.createWriter(System.out);
      writer.writeObject(jsonObject);
      System.out.println("");
      writer.close();
    }
  }

  @FunctionalInterface
  protected interface LedgerExecutorFunction {
    void execute(ClientService service) throws Exception;
  }

  protected static String getHashHexString(String name) {
    String hashed = null;
    try {
      if (name != null) {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(name.getBytes());
        hashed = DatatypeConverter.printHexBinary(bytes);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return hashed;
  }
}
