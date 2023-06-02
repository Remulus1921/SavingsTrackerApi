package com.example.savingstrackerapi.assetType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/assettype")
public class AssetTypeController {
  private final AssetTypeService assetTypeService;

  public AssetTypeController(AssetTypeService assetTypeService) {
    this.assetTypeService = assetTypeService;
  }

  @GetMapping
  public List<AssetTypeDto> getAssetTypes() {
    return assetTypeService.getAssetTypes();
  }

}
