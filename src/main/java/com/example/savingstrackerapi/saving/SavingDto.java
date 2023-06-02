package com.example.savingstrackerapi.saving;


public record SavingDto(
        Double amount,
        String assetName,
        String assetCode
) {
}
