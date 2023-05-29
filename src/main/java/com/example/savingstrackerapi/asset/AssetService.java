package com.example.savingstrackerapi.asset;

import com.example.savingstrackerapi.assetType.AssetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.example.savingstrackerapi.asset.AssetResponse.*;
@Service
public class AssetService {
  private final AssetRepository assetRepository;
  private final AssetTypeRepository assetTypeRepository;
  private final RestTemplate restTemplate;

  @Autowired
  public AssetService(AssetRepository assetRepository,
                      RestTemplate restTemplate,
                      AssetTypeRepository assetTypeRepository) {
    this.assetRepository = assetRepository;
    this.restTemplate = restTemplate;
    this.assetTypeRepository = assetTypeRepository;
  }

  public void seedCurrencyData() {
    String apiUrl = "https://api.nbp.pl/api/exchangerates/tables/a/?format=json";
    ResponseEntity<AssetResponse[]> response = restTemplate.getForEntity(apiUrl, AssetResponse[].class);
    AssetResponse[] assetResponse = response.getBody();
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

  public List<Asset> getAssets() {
    return assetRepository.findAll();
  }
}
