package com.example.savingstrackerapi.assetType;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AssetTypeConfig {

  @Bean
  CommandLineRunner commandLineRunner(AssetTypeRepository assetTypeRepository) {
    return args ->{
      AssetType currency = new AssetType(
              "currency"
      );
      AssetType cryptocurrency = new AssetType(
              "cryptocurrency"
      );
      AssetType preciousMetal = new AssetType(
              "precious metal"
      );
      assetTypeRepository.saveAll(
              List.of(currency, cryptocurrency, preciousMetal)
      );
    };
  }
}
