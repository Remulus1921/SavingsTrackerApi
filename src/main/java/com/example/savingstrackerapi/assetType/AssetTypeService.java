package com.example.savingstrackerapi.assetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetTypeService {
  private final AssetTypeRepository assetTypeRepository;

  @Autowired
  public AssetTypeService(AssetTypeRepository assetTypeRepository) {
    this.assetTypeRepository = assetTypeRepository;
  }
  public List<AssetType> getAssetTypes() {
    return assetTypeRepository.findAll();
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
