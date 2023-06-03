package com.example.savingstrackerapi.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
  Asset findAssetByCode(String code);
  List<Asset> findAssetsByAssetType_Name(String assetType);
  Asset findAssetByName(String name);
}
