package com.example.savingstrackerapi.saving.dto;

import java.util.Date;

public record SavingValueDto (
        String name,
        Double amount,
        Double value,
        Double exchangeRate,
        String date
){
}
