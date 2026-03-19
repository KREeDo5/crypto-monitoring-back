package com.example.kripta.repositories;

import com.example.kripta.entities.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    Optional<Metrics> findTopByCoinIdOrderByUnixSecondsDesc(Long coinId);

    Optional<Metrics> findTopByCoinIdAndUnixSecondsLessThanEqualOrderByUnixSecondsDesc(Long coinId, long pastTime);

    List<Metrics> findByCoinSymbolAndUnixSecondsBetween(String symbol, Long from, Long to);

    @Query("SELECT m FROM Metrics m WHERE m.coin.symbol = :symbol ORDER BY m.unixSeconds DESC")
    List<Metrics> findTopNByCoinSymbolOrderByUnixSecondsDesc(@Param("symbol") String symbol, Pageable pageable);
}
