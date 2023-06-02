package com.example.savingstrackerapi.asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/asset")
public class AssetController {
  public final AssetService assetService;

  @Autowired
  public AssetController(AssetService assetService) {
    this.assetService = assetService;
  }

  @GetMapping("{assetType}")
  public List<AssetDto> getAssets(@PathVariable("assetType") String type) {
    return assetService.getAssets(type);
  }
}
