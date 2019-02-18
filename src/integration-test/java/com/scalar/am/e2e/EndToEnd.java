package com.scalar.am.e2e;

import com.scalar.am.AssetManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import org.junit.Assert;
import org.junit.Test;
import picocli.CommandLine;

public class EndToEnd {

  final String RESULT = "result";
  final String MESSAGE = "message";
  final String SUCCESS = "success";

  JsonObject result = null;

  public JsonObject getCommandOutput(String... args) throws UnsupportedEncodingException {
    JsonObject result = null;
    String[] commandArgs = args.length != 0 ? args : new String[] {"--help"};
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (PrintStream ps = new PrintStream(output, true, "UTF-8")) {
      PrintStream previousOut = System.out;
      System.setOut(ps);
      CommandLine.run(new AssetManager(), ps, commandArgs);
      System.setOut(previousOut);
    }
    String commandOutput = output.toString().replace("[Return]", "");
    System.out.println(commandOutput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(commandOutput));
      result = jsonReader.readObject();
      jsonReader.close();
    } catch (JsonParsingException e) {
      result = null;
    }

    return result;
  }

  @Test
  public void runEndToEnd() throws UnsupportedEncodingException {
    // init
    String address = System.getenv("SCALAR_NETWORK_ADDRESS");
    String port = System.getenv("SCALAR_NETWORK_PORT");
    result =
        getCommandOutput(
            "init", "./fixture/foo-key.pem", "./fixture/foo.pem", "-host", address, "-p", port);
    JsonObject initExpected = null;
    Assert.assertEquals(initExpected, result);
    // add-type
    result = getCommandOutput("add-type", "book");
    JsonObject addTypeExpected =
        Json.createObjectBuilder()
            .add(RESULT, SUCCESS)
            .add(MESSAGE, "type book put completed.")
            .build();
    Assert.assertEquals(addTypeExpected, result);
    // list-type
    result = getCommandOutput("list-type");
    JsonObjectBuilder listTypeExpected =
        Json.createObjectBuilder().add(RESULT, SUCCESS).add(MESSAGE, "get list completed.");
    JsonObject typesJson = Json.createObjectBuilder().add("type", "book").add("age", 0).build();
    JsonArray types = Json.createArrayBuilder().add(typesJson).build();
    listTypeExpected.add("types", types);
    Assert.assertEquals(listTypeExpected.build(), result);
    // list
    result = getCommandOutput("list", "book");
    JsonObjectBuilder listExpected = Json.createObjectBuilder();
    JsonArray book = Json.createArrayBuilder().build();
    listExpected.add(RESULT, SUCCESS).add(MESSAGE, "get list completed.").add("book", book);
    Assert.assertEquals(listExpected.build(), result);
    // add
    result = getCommandOutput("add", "book", "comicexample");
    JsonObject addExpected =
        Json.createObjectBuilder()
            .add(RESULT, SUCCESS)
            .add(MESSAGE, "asset comicexample put completed.")
            .build();
    Assert.assertEquals(addExpected, result);
    // asset-history
    result = getCommandOutput("asset-history", "book", "comicexample");
    JsonObjectBuilder assetHistoryExpected =
        Json.createObjectBuilder().add(RESULT, SUCCESS).add(MESSAGE, "get history complete.");
    JsonObjectBuilder assetHistoryJson = Json.createObjectBuilder();
    long timestamp = 0;
    JsonArray resultHistoryArray = result.getJsonArray("history");
    for (int i = 0; i < resultHistoryArray.size(); i++) {
      JsonObject resultHistoryObject = resultHistoryArray.getJsonObject(i);
      timestamp = resultHistoryObject.getJsonNumber("timestamp").longValue();
    }
    assetHistoryJson.add("timestamp", timestamp).add("status", "in-stock").add("age", 0);
    JsonArray history = Json.createArrayBuilder().add(assetHistoryJson).build();
    assetHistoryExpected.add("history", history);
    Assert.assertEquals(assetHistoryExpected.build(), result);
    // borrow
    result = getCommandOutput("borrow", "book", "comicexample");
    JsonObject borrowExpected =
        Json.createObjectBuilder().add(RESULT, SUCCESS).add(MESSAGE, "Borrowed").build();
    Assert.assertEquals(borrowExpected, result);
    // return
    result = getCommandOutput("return", "book", "comicexample");
    JsonObject returnExpected =
        Json.createObjectBuilder().add(RESULT, SUCCESS).add(MESSAGE, "Returned").build();
    Assert.assertEquals(returnExpected, result);
    // validate
    result = getCommandOutput("validate", "book");
    JsonObject validateExpected = null;
    Assert.assertEquals(validateExpected, result);
  }
}
