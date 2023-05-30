package com.example.savingstrackerapi.saving;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/saving")
public class SavingController {
  private final SavingService savingService;

  @Autowired
  public SavingController(SavingService savingService) {
    this.savingService = savingService;
  }

  @GetMapping("{userId}")
  public List<Saving> getUserSavings(HttpServletRequest request) {
    return savingService.getUserSavings(request);
  }

//  -----------> TO DO
  @PostMapping
  public void postUserSaving(@RequestBody String savingJson, HttpServletRequest request) {
    try {
      savingService.addNewSaving(saving, userId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
//  <------------
  @GetMapping
  public List<Saving> getSavings() {
    return savingService.getSavings();
  }
}
