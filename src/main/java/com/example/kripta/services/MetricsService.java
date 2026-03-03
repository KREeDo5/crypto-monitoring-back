package com.example.kripta.services;

import com.example.kripta.entities.Metrics;
import com.example.kripta.repositories.CoinRepository;
import com.example.kripta.repositories.MetricsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.results.graph.collection.internal.BagInitializer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

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
                .divide(pastPrice, 1, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}