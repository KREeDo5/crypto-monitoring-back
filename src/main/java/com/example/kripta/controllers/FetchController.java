package com.example.kripta.controllers;

import com.example.kripta.dto.CoinGeckoResponse;
import com.example.kripta.entities.Coin;
import com.example.kripta.entities.Metrics;
import com.example.kripta.services.CoinService;
import com.example.kripta.services.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/fetch")
@RequiredArgsConstructor
@Slf4j

public class FetchController {
    private final RestTemplate restTemplate;
    private final CoinService coinService;
    private final MetricsService metricsService;

    private static final String COINGECKO_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50&page=1&sparkline=false";


    @GetMapping
    public String fetchAndSave() {
        try {
            ResponseEntity<List<CoinGeckoResponse>> response = restTemplate.exchange(
                    COINGECKO_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CoinGeckoResponse>>() {}
            );

            List<CoinGeckoResponse> coinsData = response.getBody();
            if (coinsData == null || coinsData.isEmpty()) {
                return "Нет данных";
            }

            int successCount = 0;
            for (CoinGeckoResponse data : coinsData) {
                try {
                    processCoinData(data);
                    successCount++;
                } catch (Exception e) {
                    log.error("Ошибка при обработке монеты {}: {}", data.getSymbol(), e.getMessage());
                }
            }

            return "Обработано " + successCount + " монет из " + coinsData.size();
        } catch (Exception e) {
            log.error("Ошибка при запросе к API", e);
            return "Ошибка: " + e.getMessage();
        }
    }


    private void processCoinData(CoinGeckoResponse data) {
        String symbol = data.getSymbol().toUpperCase();

        Coin coin = coinService.getCoinBySymbol(symbol);

        Metrics metrics = new Metrics();
        metrics.setCoin(coin);
        metrics.setPrice(data.getCurrentPrice());
        metrics.setMarketCap(data.getMarketCap());
        metrics.setVolume24h(data.getTotalVolume());
        metrics.setUnixSeconds(Instant.parse(data.getLastUpdated()).getEpochSecond());
        metrics.setFetchedAt(Instant.now());

        metricsService.saveMetrics(metrics);
    }
}
