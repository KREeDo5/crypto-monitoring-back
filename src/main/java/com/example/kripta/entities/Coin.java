package com.example.kripta.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "coin")
@Getter
@Setter
@NoArgsConstructor

public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String symbol;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "coingecko_id", unique = true)
    private String coingeckoId;

    @Column(name = "description", unique = true)
    private String description;
}
