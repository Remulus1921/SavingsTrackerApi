package com.example.savingstrackerapi.assetType;


import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AssetTypeDtoMapper implements Function<AssetType, AssetTypeDto> {

  @Override
  public AssetTypeDto apply(AssetType assetType) {
    return new AssetTypeDto(
            assetType.getName()
    );
  }
}
