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


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  }

  public void seedPreciousMetalData() {
    Asset zloto = new Asset();
    zloto.setName("Złoto");
    zloto.setCode("XAU");
    zloto.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(zloto);

    Asset srebro = new Asset();
    zloto.setName("Srebro");
    zloto.setCode("XAG");
    zloto.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(srebro);

    Asset platyna = new Asset();
    zloto.setName("Platyna");
    zloto.setCode("XPL");
    zloto.setAssetType(assetTypeRepository.findByName("precious_metal"));
    assetRepository.save(platyna);
  }

  public List<AssetDto> getAssets(String type) {
    return assetRepository.findAssetsByAssetType_Name(type)
            .stream().map(assetDtoMapper)
            .toList();
  }

  public List<AssetMonthValueDto> getMonthValue(String assetName) {
    Asset asset = assetRepository.findAssetByName(assetName);
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

        break;
      case "precious_metal":
        precious_metalUrl = "https://api.metalpriceapi.com/v1/timeframe?api_key=5ddd710cdf18ec77141a4d0b38f813bc&start_date="+formattedDatePast +"&end_date="+formattedDateCurrent+"&base=PLN"+"&currencies="+asset.getCode();
        ResponseEntity<String> responsePrecious_metal = restTemplate.getForEntity(precious_metalUrl, String.class);
        String responseBody = responsePrecious_metal.getBody();

        ObjectMapper objectMapper = new ObjectMapper();

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

            assetMonthValueDto.add(new AssetMonthValueDto(date, value));

          });


        break;

      default:
        throw new RuntimeException("Provided wrong Asset Type: " + asset.getAssetType().getName());
    }
    return assetMonthValueDto;
  }
}
