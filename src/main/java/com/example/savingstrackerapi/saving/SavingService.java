package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.AssetRepository;
import com.example.savingstrackerapi.config.JwtService;
import com.example.savingstrackerapi.saving.dto.SavingDto;
import com.example.savingstrackerapi.saving.dto.SavingValueDto;
import com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency;
import com.example.savingstrackerapi.user.User;
import com.example.savingstrackerapi.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency.*;

@Service
public class SavingService {
  private final SavingRepository savingRepository;
  private final SavingDtoMapper savingDtoMapper;
  private final AssetRepository assetRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final RestTemplate restTemplate;

  @Autowired
  public SavingService(SavingRepository savingRepository,
                       SavingDtoMapper savingDtoMapper,
                       AssetRepository assetRepository,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RestTemplate restTemplate) {
    this.savingRepository = savingRepository;
    this.savingDtoMapper = savingDtoMapper;
    this.assetRepository = assetRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.restTemplate = restTemplate;
  }

  public List<SavingDto> getUserSavings(HttpServletRequest request) {
    String userEmail = extractEmail(request);
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

  public SavingValueDto getSavingValue(String assetName, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    User user = this.userRepository.findByEmail(userEmail).orElseThrow();
    String url;
    SavingValueDto savingValueDto = null;
    Saving saving = user.getSavingList()
            .stream()
            .filter(s -> s.getAsset().getName().equals(assetName))
            .findFirst()
            .orElseThrow();

    switch (saving.getAsset().getAssetType().getName()) {
      case "currency":
        url = "https://api.nbp.pl/api/exchangerates/rates/c/" + saving.getAsset().getCode() + "/?format=json";
        ResponseEntity<AssetMonthResponseCurrency> response = restTemplate.getForEntity(url, AssetMonthResponseCurrency.class);
        AssetMonthResponseCurrency savingResponse = response.getBody();

        assert savingResponse != null;
        List<Rate> rates = savingResponse.getRates();

        savingValueDto = new SavingValueDto(saving.getAsset().getName(),
                                            saving.getAmount(),
                                            (saving.getAmount()*rates.get(0).getAsk()),
                                            rates.get(0).getAsk(),
                                            rates.get(0).getEffectiveDate());
        break;
      case "cryptocurrency":

        break;
      case "precious metal":

        break;

    }

    return savingValueDto;
  }

  public void addNewSaving(String savingJson, HttpServletRequest request) throws Exception {
    String userEmail = extractEmail(request);
    String asset;
    double amount;
    Saving newSaving = new Saving();

    User user = this.userRepository.findByEmail(userEmail).orElseThrow();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(savingJson);

      asset = jsonNode.get("Asset").asText();
      amount = jsonNode.get("Amount").asDouble();

      Saving duplicateSaving = savingRepository.findAll()
              .stream()
              .filter(saving -> saving.getAsset().getCode().equals(asset)).findFirst().orElse(null);
      if(duplicateSaving != null)
      {
        duplicateSaving.setAmount(duplicateSaving.getAmount() + amount);

        savingRepository.save(duplicateSaving);
      } else {
        newSaving.setAmount(amount);
        newSaving.setUser(user);
        newSaving.setAsset(assetRepository.findAssetByCode(asset));

        savingRepository.save(newSaving);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateSaving(String savingData, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    String assetCode;
    double amount;

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

  public void deleteSaving(String assetCode, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    User user = this.userRepository.findByEmail(userEmail).orElseThrow();

    Saving savingToDelete = user.getSavingList()
            .stream()
            .filter(saving -> saving.getAsset().getCode().equals(assetCode.toUpperCase()))
            .findFirst().orElseThrow();

    savingRepository.delete(savingToDelete);
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

    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }

    return userEmail;
  }
}
