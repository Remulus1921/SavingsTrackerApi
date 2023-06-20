package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.Asset;
import com.example.savingstrackerapi.asset.AssetRepository;
import com.example.savingstrackerapi.asset.dto.AssetMonthValueDto;
import com.example.savingstrackerapi.config.JwtService;
import com.example.savingstrackerapi.saving.dto.SavingDto;
import com.example.savingstrackerapi.saving.dto.SavingValueDto;
import com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency;
import com.example.savingstrackerapi.user.User;
import com.example.savingstrackerapi.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency.*;

@Service
public class SavingService {
  private final SavingRepository savingRepository;
  private final AssetRepository assetRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final RestTemplate restTemplate;

  @Autowired
  public SavingService(SavingRepository savingRepository,
                       AssetRepository assetRepository,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RestTemplate restTemplate) {
    this.savingRepository = savingRepository;
    this.assetRepository = assetRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.restTemplate = restTemplate;
  }

  public List<SavingDto> getUserSavings(HttpServletRequest request) {
    String userEmail = extractEmail(request);
    List<Saving> savings = new ArrayList<>();
    List<SavingDto> savingsDto = new ArrayList<>();
    User user = this.userRepository.findByEmail(userEmail).orElse(null);

    if (user != null)
    {
      savings = user.getSavingList();
      for (var saving:
           savings) {
          SavingValueDto savingValue = getSavingValue(saving);
          SavingDto savingDto = new SavingDto(saving.getAmount(),saving.getAsset().getName(),saving.getAsset().getCode(),savingValue.value(),savingValue.exchangeRate());
        savingsDto.add(savingDto);
      }

    }

    return savingsDto;
  }

  public SavingValueDto getSavingValueRequest(String assetName, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    User user = this.userRepository.findByEmail(userEmail).orElseThrow();
    Saving saving = user.getSavingList()
            .stream()
            .filter(s -> s.getAsset().getCode().equals(assetName))
            .findFirst()
            .orElseThrow();
    return getSavingValue(saving);
  }

  public SavingValueDto getSavingValue(Saving saving) {
    String url;
    String precious_metalUrl;
    ObjectMapper objectMapper = new ObjectMapper();

    switch (saving.getAsset().getAssetType().getName()) {
      case "currency":
        url = "https://api.nbp.pl/api/exchangerates/rates/c/" + saving.getAsset().getCode() + "/?format=json";
        ResponseEntity<AssetMonthResponseCurrency> response = restTemplate.getForEntity(url, AssetMonthResponseCurrency.class);
        AssetMonthResponseCurrency savingResponse = response.getBody();

        assert savingResponse != null;
        List<Rate> rates = savingResponse.getRates();

        return new SavingValueDto(saving.getAsset().getName(),
                saving.getAmount(),
                (saving.getAmount() * rates.get(0).getAsk()),
                rates.get(0).getAsk(),
                rates.get(0).getEffectiveDate());
      case "cryptocurrency":
        AtomicReference<Double> USD = new AtomicReference<>((double) 4);
        String cryptocurrencyApiUrl = "https://api.coincap.io/v2/rates/"+saving.getAsset().getName();
        ResponseEntity<String> responseCryptocurrency = restTemplate.getForEntity(cryptocurrencyApiUrl, String.class);
        String responseBodyCryptocurrency = responseCryptocurrency.getBody();

        String usd_url = "http://api.nbp.pl/api/exchangerates/rates/c/usd/last/1/?format=json";
        ResponseEntity<String> usdResp = restTemplate.getForEntity(usd_url, String.class);
        String usdBody = usdResp.getBody();

        try {
          JsonNode usdNode = objectMapper.readTree(usdBody);
          JsonNode usdRatesNode = usdNode.get("rates");
          usdRatesNode.fields().forEachRemaining(entry -> {
            JsonNode usdValue = usdRatesNode.get("ask");
            USD.set(usdValue.asDouble());
          });
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }

        JsonNode jsonNodeCryptocurrency = null;
        try {
          jsonNodeCryptocurrency = objectMapper.readTree(responseBodyCryptocurrency);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        JsonNode dataNode = jsonNodeCryptocurrency.get("data");
        double finalUSD = USD.get();
        double valueCrypto = dataNode.get("rateUsd").asDouble();

        JsonNode timestampJsonCrypto = jsonNodeCryptocurrency.get("timestamp");
        long timestampCrypto = Long.parseLong(timestampJsonCrypto.asText());
        Date dateCrypto = new Date(timestampCrypto);
        SimpleDateFormat dateFormatCrypto = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateCrypto = dateFormatCrypto.format(dateCrypto);


        return new SavingValueDto(
                saving.getAsset().getName(),
                saving.getAmount(),
                saving.getAmount() * valueCrypto * finalUSD,
                valueCrypto,
                formattedDateCrypto);
      case "precious_metal":
        precious_metalUrl = "https://api.metalpriceapi.com/v1/latest?api_key=c1be1a1b4aafc7110155ccddcf954005&base=PLN&currencies="+saving.getAsset().getCode();
        ResponseEntity<String> responsePrecious_metal = restTemplate.getForEntity(precious_metalUrl, String.class);
        String responseBody = responsePrecious_metal.getBody();
        JsonNode jsonNode = null;
        try {
          jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        JsonNode ratesNode = jsonNode.get("rates");
        JsonNode timestampJson = jsonNode.get("timestamp");
        JsonNode rateNode = ratesNode.get(saving.getAsset().getCode());

        double value = 1/rateNode.doubleValue();

        long timestamp = Long.parseLong(timestampJson.asText());
        long milliseconds = timestamp * 1000;
        Date date = new Date(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);

        return new SavingValueDto(
                saving.getAsset().getName(),
                saving.getAmount(),
                saving.getAmount() * value,
                value,
                formattedDate);
    }
    return null;
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
        Asset assetObject = assetRepository.findAssetByCode(asset);
        if (assetObject == null){ throw new RuntimeException("Asset is not supported");}
        newSaving.setAsset(assetObject);

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

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public void deleteSaving(String assetCode, HttpServletRequest request) {
    String userEmail = extractEmail(request);
    User user = this.userRepository.findByEmail(userEmail).orElseThrow();

    List<Saving> savings = user.getSavingList();

    Saving savingToDelete = savings
            .stream()
            .filter(saving -> saving.getAsset().getCode().equals(assetCode.toUpperCase()))
            .findFirst().orElseThrow();

    savings.remove(savingToDelete);

    savingRepository.deleteById(savingToDelete.getId());
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
