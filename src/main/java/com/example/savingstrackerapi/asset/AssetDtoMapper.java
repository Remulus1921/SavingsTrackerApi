package com.example.savingstrackerapi.asset;

import com.example.savingstrackerapi.asset.dto.AssetDto;
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
