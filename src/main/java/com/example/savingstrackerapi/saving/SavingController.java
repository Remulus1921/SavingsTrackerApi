package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.saving.dto.SavingDto;
import com.example.savingstrackerapi.saving.dto.SavingValueDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/saving")
public class SavingController {
  private final SavingService savingService;

  @Autowired
  public SavingController(SavingService savingService) {
    this.savingService = savingService;
  }

  @GetMapping
  public List<SavingDto> getUserSavings(HttpServletRequest request) {
    return savingService.getUserSavings(request);
  }

  @GetMapping("{assetCode}")
  public SavingValueDto getSavingValue(@PathVariable("assetCode") String assetName, HttpServletRequest request) {
    return savingService.getSavingValue(assetName, request);
  }

  @PostMapping
  public void postUserSaving(@RequestBody String saving, HttpServletRequest request) {
    try {
      savingService.addNewSaving(saving, request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @PutMapping()
  public void updateUserSaving(@RequestBody String savingData, HttpServletRequest request) {
    savingService.updateSaving(savingData, request);
  }

  @DeleteMapping("{assetCode}")
  public void deleteSaving(@PathVariable("assetCode") String assetCode, HttpServletRequest request) {
    savingService.deleteSaving(assetCode, request);
  }
}
