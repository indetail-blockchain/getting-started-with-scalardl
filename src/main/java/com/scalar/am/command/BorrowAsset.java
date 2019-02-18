package com.scalar.am.command;

import com.scalar.am.contract.StatusChangeContract;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import picocli.CommandLine;

@CommandLine.Command(name = "borrow", description = "Borrow the asset")
public class BorrowAsset extends LedgerClientExecutor implements Runnable {
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
    JsonObject argument =
        Json.createObjectBuilder()
            .add(StatusChangeContract.ASSET_ID, id)
            .add(StatusChangeContract.TIMESTAMP, new Date().getTime())
            .add(StatusChangeContract.STATUS, StatusChangeContract.ON_LOAN)
            .build();

    executeContract(StatusChangeContract.class.getCanonicalName(), argument);
  }
}
