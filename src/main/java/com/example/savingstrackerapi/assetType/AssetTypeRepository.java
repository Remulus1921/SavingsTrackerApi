package com.example.savingstrackerapi.assetType;

import com.example.savingstrackerapi.assetType.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, UUID> {

  AssetType findByName (String name);
}
