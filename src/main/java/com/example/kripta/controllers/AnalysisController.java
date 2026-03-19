package com.example.kripta.controllers;

import com.example.kripta.dto.AnalysisResponse;
import com.example.kripta.dto.Period;
import com.example.kripta.services.CryptoAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final CryptoAnalysisService analysisService;

    @GetMapping("/{symbol}")
    public AnalysisResponse getAnalysis(@PathVariable String symbol,
                                        @RequestParam(defaultValue = "DAY") Period period) {
        String analysis = analysisService.getAnalysisForPeriod(symbol, period);
        return new AnalysisResponse(analysis);
    }
}
