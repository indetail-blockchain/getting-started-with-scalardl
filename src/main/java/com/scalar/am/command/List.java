package com.scalar.am.command;

import com.scalar.am.contract.ListContract;
import javax.json.Json;
import javax.json.JsonObject;
import picocli.CommandLine;

/** This class defines the behaviour of <em>list</em> CLI command */
@CommandLine.Command(name = "list", description = "List the assets of the given type")
public class List extends LedgerClientExecutor implements Runnable {

  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  boolean help;

  @CommandLine.Parameters(
      index = "0",
      paramLabel = "asset_type",
      description = "the asset type to get, e.g. : book")
  private String type;

  @Override
  public void run() {
    JsonObject argument = Json.createObjectBuilder().add(ListContract.TYPE, type).build();
    executeContract(ListContract.class.getCanonicalName(), argument);
  }
}
