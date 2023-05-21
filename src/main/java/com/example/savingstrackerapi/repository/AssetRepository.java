package com.example.savingstrackerapi.repository;

import com.example.savingstrackerapi.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

}
