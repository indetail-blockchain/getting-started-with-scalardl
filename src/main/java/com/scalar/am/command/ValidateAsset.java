package com.scalar.am.command;

import picocli.CommandLine;

@CommandLine.Command(
    name = "validate",
    description = "validate if an asset data has not been tampered")
public class ValidateAsset extends LedgerClientExecutor implements Runnable {
  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  boolean help;

  @CommandLine.Parameters(
      arity = "1..*",
      paramLabel = "asset",
      description = "the asset <type> <name> or <id> directly")
  private String[] arguments;

  @Override
  public void run() {
    String id =
        (arguments.length == 1)
            ? arguments[0]
            : getHashHexString(arguments[0] + "_" + arguments[1]);

    validateAsset(id);
  }
}
