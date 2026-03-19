package com.example.kripta.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CoinWithMetrics {
    private String name;
    private String symbol;
    private BigDecimal price;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private BigDecimal hourChange;
    private BigDecimal dayChange;
    private BigDecimal weekChange;
    private String description;
}
