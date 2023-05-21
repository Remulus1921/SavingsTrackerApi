package com.example.savingstrackerapi.service;

import com.example.savingstrackerapi.model.Saving;
import com.example.savingstrackerapi.repository.AssetRepository;
import com.example.savingstrackerapi.repository.AssetTypeRepository;
import com.example.savingstrackerapi.repository.SavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SavingService {
  private final SavingRepository savingRepository;
  private final AssetRepository assetRepository;
  private final AssetTypeRepository assetTypeRepository;

  @Autowired
  public SavingService(SavingRepository savingRepository, AssetRepository assetRepository, AssetTypeRepository assetTypeRepository) {
    this.savingRepository = savingRepository;
    this.assetRepository = assetRepository;
    this.assetTypeRepository = assetTypeRepository;
  }

  public List<Saving> getUserSavings(UUID userId) {
    return savingRepository.findUserSavings(userId);
  }
}
