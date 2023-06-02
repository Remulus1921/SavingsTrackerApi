package com.example.savingstrackerapi.saving;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SavingDtoMapper implements Function<Saving, SavingDto> {
  @Override
  public SavingDto apply(Saving saving) {
    return new SavingDto(
            saving.getAmount(),
            saving.getAsset().getName(),
            saving.getAsset().getCode()
    );
  }
}
