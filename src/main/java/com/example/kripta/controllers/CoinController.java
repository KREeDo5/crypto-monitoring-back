package com.example.kripta.controllers;

import com.example.kripta.dto.CoinWithMetrics;
import com.example.kripta.entities.Coin;
import com.example.kripta.services.CoinService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor

public class CoinController {
    private final CoinService coinService;

    @GetMapping("/get_all")
    public List<Coin> getAllCoins() {
        return coinService.getAllCoins();
    }

    @GetMapping("/with_metrics")
    public List<CoinWithMetrics> getCoinsWithMetrics() {
        return coinService.getCoinsWithMetrics();
    }
}
