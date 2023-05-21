package com.example.savingstrackerapi.repository;

import com.example.savingstrackerapi.model.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetTypeRepository extends JpaRepository<AssetType, UUID> {
}
