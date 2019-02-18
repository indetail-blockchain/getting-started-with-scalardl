package com.scalar.am.command;

import com.scalar.am.contract.ListTypeContract;
import javax.json.Json;
import picocli.CommandLine;

@CommandLine.Command(name = "list-type", description = "List the existing types of asset")
public class ListType extends LedgerClientExecutor implements Runnable {

  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  boolean help;

  @Override
  public void run() {
    executeContract(ListTypeContract.class.getCanonicalName(), Json.createObjectBuilder().build());
  }
}
