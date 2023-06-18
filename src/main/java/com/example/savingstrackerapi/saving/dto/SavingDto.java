package com.example.savingstrackerapi.saving.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

public record SavingDto(
        @JsonFormat(pattern = "0.00")
        Double amount,
        String assetName,
        String assetCode,
        Double value,
        Double exchangeRate
) {
}
