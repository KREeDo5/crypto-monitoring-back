package com.example.kripta.repositories;

import com.example.kripta.dto.Period;
import com.example.kripta.entities.AnalysisCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisCacheRepository extends JpaRepository<AnalysisCache, Long> {

    Optional<AnalysisCache> findTopBySymbolAndPeriodOrderByCreatedAtDesc(String symbol, Period period);

    Optional<AnalysisCache> findBySymbolAndPeriod(String symbol, Period period);
}

