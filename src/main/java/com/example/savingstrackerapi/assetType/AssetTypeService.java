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
}
