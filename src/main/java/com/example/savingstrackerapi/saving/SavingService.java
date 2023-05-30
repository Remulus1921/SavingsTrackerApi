package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.AssetRepository;
import com.example.savingstrackerapi.assetType.AssetTypeRepository;
import com.example.savingstrackerapi.config.JwtService;
import com.example.savingstrackerapi.user.User;
import com.example.savingstrackerapi.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SavingService {
  private final SavingRepository savingRepository;
  private final AssetRepository assetRepository;
  private final AssetTypeRepository assetTypeRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Autowired
  public SavingService(SavingRepository savingRepository,
                       AssetRepository assetRepository,
                       AssetTypeRepository assetTypeRepository,
                       UserRepository userRepository,
                       JwtService jwtService) {
    this.savingRepository = savingRepository;
    this.assetRepository = assetRepository;
    this.assetTypeRepository = assetTypeRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  public List<Saving> getUserSavings(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String Token;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      throw new IllegalStateException("No user token");
    }
    Token = authHeader.substring(7);
    userEmail = jwtService.extractUsername(Token);
    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }
    var user = this.userRepository.findByEmail(userEmail).orElseThrow();

    return savingRepository.findUserSavings(user.getId());
  }
//  ----------> Done
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
//    List<Saving> savingList = new ArrayList<>(user.getSavingList());
//    savingList.add(newSaving);

    savingRepository.save(newSaving);
  }
//  <----------
  public List<Saving> getSavings() {
    return savingRepository.findAll();
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
