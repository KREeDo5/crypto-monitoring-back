package com.example.kripta.services;

import com.example.kripta.dto.CoinDto;
import com.example.kripta.dto.CoinWithMetrics;
import com.example.kripta.entities.Coin;
import com.example.kripta.entities.Metrics;
import com.example.kripta.repositories.CoinRepository;

import com.example.kripta.repositories.MetricsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CoinService {
    private final CoinRepository coinRepository;
    private final MetricsRepository metricsRepository;
    private final MetricsService metricsService;

    public List<Coin> getAllCoins() {
        return coinRepository.findAll();
    }

    @Transactional
    public Coin saveCoin(Coin coin) {
        return coinRepository.save(coin);
    }

    public Coin getCoinBySymbol(String symbol) {
        return coinRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Монета с символом " + symbol + " не найдена"));
    }

    public List<CoinWithMetrics> getCoinsWithMetrics() {
        List<Coin> coins = coinRepository.findAll();
        List<CoinWithMetrics> result = new ArrayList<>();

        for (Coin coin : coins) {
            Optional<Metrics> latestData = metricsRepository.findTopByCoinIdOrderByUnixSecondsDesc(coin.getId());

            CoinWithMetrics.CoinWithMetricsBuilder builder = CoinWithMetrics.builder()
                    .name(coin.getName()).symbol(coin.getSymbol());

            latestData.ifPresent(latestMetrics -> builder.price(latestMetrics.getPrice())
                    .marketCap(latestMetrics.getMarketCap())
                    .volume24h(latestMetrics.getVolume24h())
                    .hourChange(metricsService.calculatePriceChange(coin.getId(), 3600))
                    .dayChange(metricsService.calculatePriceChange(coin.getId(), 86400))
                    .weekChange(metricsService.calculatePriceChange(coin.getId(), 604800)));
            result.add(builder.build());
        }
        return result;
    }

    public List<String> getAllCoinGeckoIds() {
        return coinRepository.findAll().stream()
                .map(Coin::getCoingeckoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public CoinDto getCoinInfo(String symbol) {
        Coin coin = coinRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Монета не найдена: " + symbol));
        CoinDto coinDto = new CoinDto();
        coinDto.setSymbol(coin.getSymbol());
        coinDto.setName(coin.getName());
        coinDto.setDescription(coin.getDescription());
        return coinDto;
    }
}