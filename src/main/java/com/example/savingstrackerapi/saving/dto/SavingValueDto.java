package com.example.savingstrackerapi.saving.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record SavingValueDto (
        String name,
        @JsonFormat(pattern = "0.00")
        Double amount,
        @JsonFormat(pattern = "0.00")
        Double value,
        @JsonFormat(pattern = "0.00")
        Double exchangeRate,
        String date
){
}
