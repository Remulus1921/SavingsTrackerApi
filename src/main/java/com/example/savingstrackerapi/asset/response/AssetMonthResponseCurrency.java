package com.example.savingstrackerapi.asset.response;

import lombok.Data;

import java.util.List;

@Data
public class AssetMonthResponseCurrency {
  private String table;
  private String currency;
  private String code;
  private List<Rate> rates;

  public static class Rate {
    private String no;
    private String effectiveDate;
    private double bid;
    private double ask;

    public String getEffectiveDate() {
      return effectiveDate;
    }

    public double getBid() {
      return bid;
    }

    public double getAsk() {
      return ask;
    }

  }
}

