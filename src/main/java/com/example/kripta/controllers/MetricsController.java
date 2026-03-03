package com.example.kripta.controllers;

import com.example.kripta.entities.Metrics;
import com.example.kripta.services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor

public class MetricsController {
    private final MetricsService metricsService;

    @PostMapping
    public Metrics saveMetrics(@RequestBody Metrics metrics) {
        return metricsService.saveMetrics(metrics);
    }
}
