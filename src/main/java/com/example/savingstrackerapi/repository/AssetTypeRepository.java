package com.example.savingstrackerapi.repository;

import com.example.savingstrackerapi.model.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, UUID> {
}
