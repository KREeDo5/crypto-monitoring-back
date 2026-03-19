package com.example.kripta.controllers;

import com.example.kripta.dto.MetricsDto;
import com.example.kripta.dto.Period;
import com.example.kripta.entities.Metrics;
import com.example.kripta.services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor

public class MetricsController {
    private final MetricsService metricsService;

    @PostMapping("/admin/post_metrics")
    public Metrics saveMetrics(@RequestBody Metrics metrics) {
        return metricsService.saveMetrics(metrics);
    }

    @GetMapping("/{symbol}")
    public List<MetricsDto> getMetrics(@PathVariable String symbol, @RequestParam Period period) {
        return metricsService.getMetrics(symbol, period);
    }
}
