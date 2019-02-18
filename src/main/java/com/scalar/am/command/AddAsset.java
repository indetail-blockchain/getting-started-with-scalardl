package com.scalar.am.command;

import com.scalar.am.contract.AddAssetContract;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import picocli.CommandLine;

/** This class defines the behaviour of <em>add</em> CLI command */
@CommandLine.Command(name = "add", description = "Add the asset")
public class AddAsset extends LedgerClientExecutor implements Runnable {
  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display this help and exit")
  boolean help;

  @CommandLine.Parameters(
      index = "0",
      paramLabel = "asset_type",
      description = "the asset type, e.g : book ")
  private String type;

  @CommandLine.Parameters(
      index = "1",
      paramLabel = "asset",
      description = "the asset description, e.g : Lord of the Rings")
  private String asset;

  @Override
  public void run() {
    JsonObject argument =
        Json.createObjectBuilder()
            .add(AddAssetContract.TYPE, type)
            .add(AddAssetContract.ASSET, asset)
            .add(AddAssetContract.TIMESTAMP, new Date().getTime())
            .add(AddAssetContract.ID, getHashHexString(type + "_" + asset))
            .build();

    executeContract(AddAssetContract.class.getCanonicalName(), argument);
  }
}
