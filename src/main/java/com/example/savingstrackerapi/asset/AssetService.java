package com.example.savingstrackerapi.asset;

import com.example.savingstrackerapi.asset.dto.AssetDto;
import com.example.savingstrackerapi.asset.dto.AssetMonthValueDto;
import com.example.savingstrackerapi.asset.response.AssetMonthResponseCurrency;
import com.example.savingstrackerapi.asset.response.AssetResponseCurrency;
import com.example.savingstrackerapi.assetType.AssetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

  }

  public List<AssetDto> getAssets(String type) {
    return assetRepository.findAssetsByAssetType_Name(type)
            .stream().map(assetDtoMapper)
            .toList();
  }

  public List<AssetMonthValueDto> getMonthValue(String assetName) {
    Asset asset = assetRepository.findAssetByName(assetName);
    List<AssetMonthValueDto> assetMonthValueDto = new ArrayList<>();
    String url;


    switch (asset.getAssetType().getName()) {
      case "currency":
        url = "https://api.nbp.pl/api/exchangerates/rates/c/" + asset.getCode() + "/last/30/?format=json";
        ResponseEntity<AssetMonthResponseCurrency> response = restTemplate.getForEntity(url, AssetMonthResponseCurrency.class);
        AssetMonthResponseCurrency assetResponse = response.getBody();

        assert assetResponse != null;
        List<AssetMonthResponseCurrency.Rate> rates = assetResponse.getRates();

        for (var rate: rates) {

          assetMonthValueDto.add(new AssetMonthValueDto(rate.getEffectiveDate(), rate.getAsk(), rate.getBid()));
        }

        break;
      case "cryptocurrency":

        break;
      case "precious metal":

        break;

      default:
        throw new RuntimeException("Provided wrong Asset Type: " + asset.getAssetType().getName());
    }
    return assetMonthValueDto;
  }
}
