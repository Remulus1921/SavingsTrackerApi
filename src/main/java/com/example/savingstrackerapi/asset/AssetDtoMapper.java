package com.example.savingstrackerapi.asset;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AssetDtoMapper implements Function<Asset, AssetDto> {

  @Override
  public AssetDto apply(Asset asset) {
    return new AssetDto(
            asset.getName(),
            asset.getCode()
    );
  }
}
