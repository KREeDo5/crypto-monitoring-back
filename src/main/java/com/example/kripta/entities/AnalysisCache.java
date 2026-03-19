package com.example.kripta.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "analysis_cache",
    uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "period"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.example.kripta.dto.Period period;

    @Column(columnDefinition = "TEXT")
    private String analysis;

    @Column(nullable = false)
    private long createdAt;
}