package com.scalar.am.command;

import com.scalar.am.contract.AssetHistoryContract;
import javax.json.Json;
import javax.json.JsonObject;
import picocli.CommandLine;

/** This class defines the behaviour of <em>asset-history</em> CLI command */
@CommandLine.Command(name = "asset-history", description = "List the assets of the given asset-id")
public class AssetHistory extends LedgerClientExecutor implements Runnable {

  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  boolean help;

  @CommandLine.Parameters(
      arity = "1..*",
      paramLabel = "arguments",
      description = "can be <type> <name> or <id> directly.")
  private String[] arguments;

  @Override
  public void run() {
    String id =
        (arguments.length == 1)
            ? arguments[0]
            : getHashHexString(arguments[0] + "_" + arguments[1]);
    JsonObject argument = Json.createObjectBuilder().add(AssetHistoryContract.ID, id).build();
    executeContract(AssetHistoryContract.class.getCanonicalName(), argument);
  }
}
