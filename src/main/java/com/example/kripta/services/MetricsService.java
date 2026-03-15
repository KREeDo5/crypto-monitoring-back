package com.example.kripta.services;

import com.example.kripta.dto.Period;
import com.example.kripta.entities.Metrics;
import com.example.kripta.repositories.CoinRepository;
import com.example.kripta.repositories.MetricsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.results.graph.collection.internal.BagInitializer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor

public class MetricsService {
    private final MetricsRepository metricsRepository;
    private final CoinRepository coinRepository;

    @Transactional
    public Metrics saveMetrics(Metrics metrics) {
        return metricsRepository.save(metrics);
    }

    public BigDecimal calculatePriceChange(Long coinId, int timeInterval) {
        Optional<Metrics> latestData = metricsRepository.findTopByCoinIdOrderByUnixSecondsDesc(coinId);
        if (latestData.isEmpty()) return null;

        Metrics latestMetrics = latestData.get();
        long currentTime = latestMetrics.getUnixSeconds();
        long pastTime = currentTime - timeInterval;

        Optional<Metrics> pastData = metricsRepository.findTopByCoinIdAndUnixSecondsLessThanEqualOrderByUnixSecondsDesc(coinId, pastTime);
        if (pastData.isEmpty()) return null;

        BigDecimal currentPrice = latestMetrics.getPrice();
        BigDecimal pastPrice = pastData.get().getPrice();
        return currentPrice.subtract(pastPrice)
                .divide(pastPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public List<Metrics> getMetrics(String symbol, Period period) {
        long now = Instant.now().getEpochSecond();
        long from = now - getPeriodSeconds(period);

        return metricsRepository.findByCoinSymbolAndUnixSecondsBetween(symbol, from, now);
    }

    private long getPeriodSeconds(Period period) {
        return switch (period) {
            case HOUR -> 3600;
            case HOURS_6 -> 6 * 3600;
            case DAY -> 86400;
            case WEEK -> 7 * 86400;
            case MONTH -> 30 * 86400;
        };
    }
}