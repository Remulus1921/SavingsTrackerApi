package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.AssetRepository;
import com.example.savingstrackerapi.config.JwtService;
import com.example.savingstrackerapi.user.User;
import com.example.savingstrackerapi.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SavingService {
  private final SavingRepository savingRepository;
  private final SavingDtoMapper savingDtoMapper;
  private final AssetRepository assetRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Autowired
  public SavingService(SavingRepository savingRepository,
                       SavingDtoMapper savingDtoMapper,
                       AssetRepository assetRepository,
                       UserRepository userRepository,
                       JwtService jwtService) {
    this.savingRepository = savingRepository;
    this.savingDtoMapper = savingDtoMapper;
    this.assetRepository = assetRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  public List<SavingDto> getUserSavings(HttpServletRequest request) {
    String userEmail = extractEmail(request);
    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }
    List<SavingDto> savings = new ArrayList<>();
    User user = this.userRepository.findByEmail(userEmail).orElse(null);
    if (user != null)
    {
      savings = user.getSavingList()
              .stream().map(savingDtoMapper)
              .toList();
    }

    return savings;
  }

  public void addNewSaving(String savingJson, HttpServletRequest request) throws Exception {
    String userEmail = extractEmail(request);
    String asset;
    double amount;
    Saving newSaving = new Saving();

    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }
    var user = this.userRepository.findByEmail(userEmail).orElseThrow();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(savingJson);

      asset = jsonNode.get("Asset").asText();
      amount = jsonNode.get("Amount").asDouble();

      newSaving.setAmount(amount);
      newSaving.setUser(user);
      newSaving.setAsset(assetRepository.findAssetByCode(asset));

    } catch (Exception e) {
      e.printStackTrace();
    }

    savingRepository.save(newSaving);
  }

  public List<SavingDto> getSavings() {
    return savingRepository.findAll()
            .stream().map(saving -> new SavingDto(
                    saving.getAmount(),
                    saving.getAsset().getName(),
                    saving.getAsset().getCode()
            ))
            .toList();

  }

  public void updateSaving(String savingData, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    String assetCode;
    double amount;

    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }

    User user = this.userRepository.findByEmail(userEmail).orElseThrow();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(savingData);

      assetCode = jsonNode.get("Asset").asText();
      amount = jsonNode.get("Amount").asDouble();

      Saving savingToUpdate = user.getSavingList()
              .stream()
              .filter(saving -> saving.getAsset().getCode().equals(assetCode))
              .findFirst()
              .orElseThrow();

      savingToUpdate.setAmount(amount);
      savingRepository.save(savingToUpdate);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String extractEmail(HttpServletRequest request) {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String Token;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      throw new IllegalStateException("No user token");
    }
    Token = authHeader.substring(7);
    userEmail = jwtService.extractUsername(Token);

    return userEmail;
  }
}
