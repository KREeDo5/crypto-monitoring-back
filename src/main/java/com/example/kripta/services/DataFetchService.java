package com.example.kripta.services;

import com.example.kripta.dto.CoinGeckoResponse;
import com.example.kripta.entities.Coin;
import com.example.kripta.entities.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataFetchService {
    private final RestTemplate restTemplate;
    private final CoinService coinService;
    private final MetricsService metricsService;

    public int fetchAndSaveData() {
        List<String> ids = coinService.getAllCoinGeckoIds();
        if (ids.isEmpty()) {
            log.warn("Нет монет с coingecko_id");
            return 0;
        }
        String idsParam = String.join(",", ids);
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=" + idsParam;

        try {
            ResponseEntity<List<CoinGeckoResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CoinGeckoResponse>>() {}
            );

            List<CoinGeckoResponse> coinsData = response.getBody();
            if (coinsData == null || coinsData.isEmpty()) {
                log.warn("API вернул пустой ответ");
                return 0;
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
            return successCount;
        } catch (Exception e) {
            log.error("Ошибка при запросе к API", e);
            return 0;
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
