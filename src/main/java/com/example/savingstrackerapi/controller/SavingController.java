package com.example.savingstrackerapi.controller;

import com.example.savingstrackerapi.model.Saving;
import com.example.savingstrackerapi.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public List<Saving> getUserSavings(@PathVariable("userId") UUID userId) {
    return savingService.getUserSavings(userId);
  }


}
