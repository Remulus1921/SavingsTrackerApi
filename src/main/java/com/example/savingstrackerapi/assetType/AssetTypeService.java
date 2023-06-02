package com.example.savingstrackerapi.assetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetTypeService {
  private final AssetTypeRepository assetTypeRepository;
  private final AssetTypeDtoMapper assetTypeDtoMapper;

  @Autowired
  public AssetTypeService(AssetTypeRepository assetTypeRepository, AssetTypeDtoMapper assetTypeDtoMapper) {
    this.assetTypeRepository = assetTypeRepository;
    this.assetTypeDtoMapper = assetTypeDtoMapper;
  }
  public List<AssetTypeDto> getAssetTypes() {
    return assetTypeRepository.findAll()
            .stream().map(assetTypeDtoMapper)
            .toList();
  }

  public void seedAssetTypes() {
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
  }
}
