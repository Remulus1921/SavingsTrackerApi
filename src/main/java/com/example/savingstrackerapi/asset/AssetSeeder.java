package com.example.savingstrackerapi.asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AssetSeeder implements ApplicationRunner {
  private final AssetService assetService;

  @Autowired
  public AssetSeeder(AssetService assetService) {
    this.assetService = assetService;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    assetService.seedCurrencyData();
    assetService.seedPreciousMetalData();
    assetService.seedCryptocurrencyData();
  }
}
