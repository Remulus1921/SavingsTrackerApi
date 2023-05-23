package com.example.savingstrackerapi.asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
  public final AssetRepository assetRepository;

  @Autowired
  public AssetService(AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
  }

  public List<Asset> getAssets() {
    return assetRepository.findAll();
  }
}
