package com.example.savingstrackerapi.assetType;

import com.example.savingstrackerapi.assetType.AssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AssetTypesSeeder implements ApplicationRunner {
  private final AssetTypeService assetTypeService;

  @Autowired
  public AssetTypesSeeder(AssetTypeService assetTypeService) {
    this.assetTypeService = assetTypeService;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    assetTypeService.seedAssetTypes();
  }
}
