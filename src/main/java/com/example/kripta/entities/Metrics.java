package com.example.kripta.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "metrics")
@Getter
@Setter
@NoArgsConstructor

public class Metrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal price;

    @Column(name = "market_cap", nullable = false, precision = 20, scale = 2)
    private BigDecimal marketCap;

    @Column(name = "volume_24h", nullable = false, precision = 20, scale = 2)
    private BigDecimal volume24h;

    @Column(name = "unix_seconds", nullable = false)
    private Long unixSeconds;

    @Column(name = "fetched_at")
    private Instant fetchedAt = Instant.now();
}
