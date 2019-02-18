package com.scalar.am;

import com.scalar.am.command.AddAsset;
import com.scalar.am.command.AddType;
import com.scalar.am.command.AssetHistory;
import com.scalar.am.command.BorrowAsset;
import com.scalar.am.command.Init;
import com.scalar.am.command.List;
import com.scalar.am.command.ListType;
import com.scalar.am.command.ReturnAsset;
import com.scalar.am.command.ValidateAsset;
import picocli.CommandLine;

/**
 * The entry point of the CLI application <br>
 * This class references the other subcommands
 */
@CommandLine.Command(
    name = "am",
    description = "Asset manager application",
    version = "1.0",
    subcommands = {
      Init.class,
      AddAsset.class,
      AddType.class,
      BorrowAsset.class,
      ListType.class,
      List.class,
      ReturnAsset.class,
      AssetHistory.class,
      ValidateAsset.class
    })
public class AssetManager implements Runnable {
  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  private boolean help;

  public static void main(String[] args) {
    // Display the help if no arguments are passed
    String[] commandArgs = args.length != 0 ? args : new String[] {"--help"};
    CommandLine.run(new AssetManager(), commandArgs);
  }

  @Override
  public void run() {}
}
