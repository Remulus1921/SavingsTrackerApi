package com.example.savingstrackerapi.asset;

import lombok.Data;

import java.util.List;

@Data
public class AssetResponse {
  private String table;
  private String no;
  private String effectiveDate;
  private List<Rate> rates;

  public static class Rate {
    private String currency;
    private String code;
    private double mid;

    public String getCurrency() {
      return currency;
    }

    public String getCode() {
      return code;
    }
  }
}
