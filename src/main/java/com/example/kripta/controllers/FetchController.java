package com.example.kripta.controllers;

import com.example.kripta.services.CoinService;
import com.example.kripta.services.DataFetchService;
import com.example.kripta.services.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/admin/fetch")
@RequiredArgsConstructor
@Slf4j

public class FetchController {
    private final RestTemplate restTemplate;
    private final CoinService coinService;
    private final MetricsService metricsService;
    private final DataFetchService dataFetchingService;

    @GetMapping
    public String fetchAndSave() {
        int count = dataFetchingService.fetchAndSaveData();
        return "Обработано " + count +  "монет";
    }
}
