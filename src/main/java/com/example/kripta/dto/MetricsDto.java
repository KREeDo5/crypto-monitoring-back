package com.example.kripta.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class MetricsDto {
    private BigDecimal price;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private Long unixSeconds;
}
