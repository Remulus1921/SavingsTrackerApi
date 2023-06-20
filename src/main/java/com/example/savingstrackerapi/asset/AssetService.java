package com.example.savingstrackerapi.asset;

import com.example.savingstrackerapi.asset.dto.AssetDto;
import com.example.savingstrackerapi.asset.dto.AssetMonthValueDto;
import com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency;
import com.example.savingstrackerapi.asset.response.AssetResponseCurrency;
import com.example.savingstrackerapi.assetType.AssetTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.savingstrackerapi.asset.response.AssetResponseCurrency.*;
@Service
public class AssetService {
  private final AssetRepository assetRepository;
  private final AssetTypeRepository assetTypeRepository;
  private final RestTemplate restTemplate;
  private final AssetDtoMapper assetDtoMapper;

  @Autowired
  public AssetService(AssetRepository assetRepository,
                      RestTemplate restTemplate,
                      AssetTypeRepository assetTypeRepository,
                      AssetDtoMapper assetDtoMapper) {
    this.assetRepository = assetRepository;
    this.restTemplate = restTemplate;
    this.assetTypeRepository = assetTypeRepository;
    this.assetDtoMapper = assetDtoMapper;
  }

  public void seedCurrencyData() {
    String apiUrl = "https://api.nbp.pl/api/exchangerates/tables/a/?format=json";
    ResponseEntity<AssetResponseCurrency[]> response = restTemplate.getForEntity(apiUrl, AssetResponseCurrency[].class);
    AssetResponseCurrency[] assetResponse = response.getBody();
    assert assetResponse != null;
    List<Rate> rates = assetResponse[0].getRates();

    Asset[] assets = new Asset[rates.size()];
    for (int i = 0; i < (long) rates.size(); i++) {
      Asset asset = new Asset();
      asset.setName(rates.get(i).getCurrency());
      asset.setCode(rates.get(i).getCode());
      asset.setAssetType(assetTypeRepository.findByName("currency"));
      assets[i] = asset;
    }
    assetRepository.saveAll(Arrays.asList(assets));
  }

  public void seedCryptocurrencyData() {
    String apiUrl = "https://api.coincap.io/v2/assets?limit=10";
    ResponseEntity<String> responseCryptocurrency = restTemplate.getForEntity(apiUrl, String.class);
    String responseBody = responseCryptocurrency.getBody();

    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode jsonNode = null;
    try {
      jsonNode = objectMapper.readTree(responseBody);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataArray = jsonNode.get("data");
    for (JsonNode dataNode : dataArray) {
      Asset asset = new Asset();
      String id = dataNode.get("id").asText();
      String symbol = dataNode.get("symbol").asText();

      asset.setName(id);
      asset.setCode(symbol);
      asset.setAssetType(assetTypeRepository.findByName("cryptocurrency"));
      assetRepository.save(asset);
    }
  }

  public void seedPreciousMetalData() {
    Asset zloto = new Asset();
    zloto.setName("Złoto");
    zloto.setCode("XAU");
    zloto.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(zloto);

    Asset srebro = new Asset();
    srebro.setName("Srebro");
    srebro.setCode("XAG");
    srebro.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(srebro);

    Asset platyna = new Asset();
    platyna.setName("Platyna");
    platyna.setCode("XPT");
    platyna.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(platyna);

    Asset pallad = new Asset();
    pallad.setName("Pallad");
    pallad.setCode("XPD");
    pallad.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(pallad);
  }

  public List<AssetDto> getAssets(String type) {
    return assetRepository.findAssetsByAssetType_Name(type)
            .stream().map(assetDtoMapper)
            .toList();
  }

  public List<AssetMonthValueDto> getMonthValue(String assetCode) {
    ObjectMapper objectMapper = new ObjectMapper();

    Asset asset = assetRepository.findAssetByCode(assetCode);
    List<AssetMonthValueDto> assetMonthValueDto = new ArrayList<>();
    String currencyUrl;
    String precious_metalUrl;

    LocalDate currentDate = LocalDate.now();

    // Odejmowanie 30 dni od obecnej daty
    LocalDate newDate = currentDate.minusDays(30);

    // Formatowanie daty do żądanego formatu
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDatePast = newDate.format(formatter);
    String formattedDateCurrent = currentDate.format(formatter);



    switch (asset.getAssetType().getName()) {
      case "currency":
        currencyUrl = "https://api.nbp.pl/api/exchangerates/rates/c/" + asset.getCode() + "/" +formattedDatePast + "/" + formattedDateCurrent + "/";
        ResponseEntity<AssetMonthResponseCurrency> responseCurrency = restTemplate.getForEntity(currencyUrl, AssetMonthResponseCurrency.class);
        AssetMonthResponseCurrency assetResponseCurrency = responseCurrency.getBody();

        assert assetResponseCurrency != null;
        List<AssetMonthResponseCurrency.Rate> ratesCurrency = assetResponseCurrency.getRates();

        for (var rate: ratesCurrency) {

          assetMonthValueDto.add(new AssetMonthValueDto(rate.getEffectiveDate(), rate.getAsk()));
        }

        break;
      case "cryptocurrency":
        AtomicReference<Double> USD = new AtomicReference<>((double) 4);
        long currentMillis = System.currentTimeMillis();
        Instant thirtyDaysAgo = Instant.ofEpochMilli(currentMillis).minus(30, ChronoUnit.DAYS);
        long thirtyDaysAgoMillis = thirtyDaysAgo.toEpochMilli();
        String cryptocurrencyApiUrl = "https://api.coincap.io/v2/assets/"+asset.getName()+"/history?interval=d1&start="+thirtyDaysAgoMillis+"&end="+currentMillis;
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
        JsonNode dataArray = jsonNodeCryptocurrency.get("data");
        double finalUSD = USD.get();
        for (JsonNode dataNode : dataArray) {
          JsonNode timestampJson = dataNode.get("time");
          long timestamp = Long.parseLong(timestampJson.asText());
          Date date = new Date(timestamp);
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          String formattedDate = dateFormat.format(date);

          double value = dataNode.get("priceUsd").asDouble();

          assetMonthValueDto.add(new AssetMonthValueDto(formattedDate, value* finalUSD));
        }
        break;
      case "precious_metal":
        precious_metalUrl = "https://api.metalpriceapi.com/v1/timeframe?api_key=5ddd710cdf18ec77141a4d0b38f813bc&start_date="+formattedDatePast +"&end_date="+formattedDateCurrent+"&base=PLN"+"&currencies="+asset.getCode();
        ResponseEntity<String> responsePrecious_metal = restTemplate.getForEntity(precious_metalUrl, String.class);
        String responseBody = responsePrecious_metal.getBody();

        JsonNode jsonNode = null;
        try {
          jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        JsonNode ratesNode = jsonNode.get("rates");
          ratesNode.fields().forEachRemaining(entry -> {
            String date = entry.getKey();
            double value = entry.getValue().get(asset.getCode()).asDouble();

            assetMonthValueDto.add(new AssetMonthValueDto(date, 1/value));

          });


        break;

      default:
        throw new RuntimeException("Provided wrong Asset Type: " + asset.getAssetType().getName());
    }
    return assetMonthValueDto;
  }
}
