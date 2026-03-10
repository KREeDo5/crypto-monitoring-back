package com.example.kripta.repositories;

import com.example.kripta.entities.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    Optional<Metrics> findTopByCoinIdOrderByUnixSecondsDesc(Long coinId);

    Optional<Metrics> findTopByCoinIdAndUnixSecondsLessThanEqualOrderByUnixSecondsDesc(Long coinId, long pastTime);

    List<Metrics> findByCoinSymbolAndUnixSecondsBetween(String symbol, Long from, Long to);
}
