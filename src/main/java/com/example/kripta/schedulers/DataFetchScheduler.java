package com.example.kripta.schedulers;

import com.example.kripta.services.DataFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataFetchScheduler {
    private final DataFetchService dataFetchService;

    @Scheduled(fixedDelay = 20000)
    public void fetchData() {
        log.info("Обновление данных");
        int count = dataFetchService.fetchAndSaveData();
        log.info("Обновление завершено. {} монет из 50", count);
    }
}
