package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.AssetDto;

public record SavingDto(
        Double amount,
        String assetName,
        String assetCode
) {
}
