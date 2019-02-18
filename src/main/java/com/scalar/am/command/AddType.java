package com.scalar.am.command;

import com.scalar.am.contract.AddTypeContract;
import javax.json.Json;
import javax.json.JsonObject;
import picocli.CommandLine;

/** This class defines the behaviour of <em>add-type</em> CLI command */
@CommandLine.Command(name = "add-type", description = "Add the asset type")
public class AddType extends LedgerClientExecutor implements Runnable {

  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  private boolean help;

  @CommandLine.Parameters(
      index = "0",
      paramLabel = "asset_type",
      description = "the asset type to add, e.g. : book")
  private String type;

  @Override
  public void run() {
    JsonObject argument = Json.createObjectBuilder().add(AddTypeContract.NAME, type).build();
    this.executeContract(AddTypeContract.class.getCanonicalName(), argument);
  }
}
