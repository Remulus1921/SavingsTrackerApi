package com.example.savingstrackerapi.saving;

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

  @PostMapping
  public void postUserSaving(@RequestBody String savingJson, HttpServletRequest request) {
    try {
      savingService.addNewSaving(savingJson, request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @GetMapping("/getAll")
  public List<SavingDto> getSavings() {
    return savingService.getSavings();
  }
}
