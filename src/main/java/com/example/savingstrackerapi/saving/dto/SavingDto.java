package com.example.savingstrackerapi.saving.dto;


public record SavingDto(
        Double amount,
        String assetName,
        String assetCode
) {
}
