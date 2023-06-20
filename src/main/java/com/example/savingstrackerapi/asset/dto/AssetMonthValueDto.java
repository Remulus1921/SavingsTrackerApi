package com.example.savingstrackerapi.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AssetMonthValueDto (
        String data,
        @JsonFormat(pattern = "0.00")
        Double price
){
}
