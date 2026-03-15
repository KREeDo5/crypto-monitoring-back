package com.example.kripta.services;

import com.example.kripta.dto.AnalysisResponse;
import com.example.kripta.dto.OpenRouterRequest;
import com.example.kripta.dto.OpenRouterResponse;
import com.example.kripta.dto.Period;
import com.example.kripta.entities.Metrics;
import com.example.kripta.repositories.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoAnalysisService {
    private final MetricsRepository metricsRepository;
    private final RestTemplate restTemplate;

    @Value("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")
    private String apiKey;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "arcee-ai/trinity-large-preview:free";

    public String getAnalysisForPeriod(String symbol, Period period) {
        int limit = getLimitForPeriod(period);
        return getAnalysisForCoin(symbol, limit);
    }

    public String getAnalysisForCoin(String symbol, int limit) {
        Pageable topN = PageRequest.of(0, limit);
        List<Metrics> metrics = metricsRepository.findTopNByCoinSymbolOrderByUnixSecondsDesc(symbol, topN);
        if (metrics.isEmpty()) {
            return "Недостаточно данных для анализа";
        }

        StringBuilder dataBuilder = new StringBuilder("price,24volume,timestamp\n");
        for (Metrics m : metrics) {
            dataBuilder.append(m.getPrice().toPlainString()).append(",")
                    .append(m.getVolume24h().toPlainString()).append(",")
                    .append(m.getUnixSeconds()).append("\n");
        }
        String dataCSV = dataBuilder.toString();

        String prompt = "Ты — профессиональный криптоаналитик с опытом работы в трейдинге и исследовании рынка.\n\n" +
                "Ниже приведены исторические данные по одной криптовалюте за последний период. Данные представлены в виде таблицы:\n" +
                "- `price` — цена монеты в USD\n" +
                "- `volume` — суточный объём торгов в USD\n" +
                "- `timestamp` — время в формате Unix (секунды)\n\n" +
                "Таблица отсортирована по возрастанию времени (от самых старых записей к новым).\n\n" +
                "Задача: проанализируй эти данные и напиши **краткий вывод (3–4 предложения)**, который включает:\n" +
                "- Основной тренд цены (росла, падала, была в боковике) за весь период.\n" +
                "- Как менялся объём (рос вместе с ценой, падал, были всплески).\n" +
                "- Если заметна взаимосвязь между объёмом и ценой (например, рост цены на высоком объёме — сигнал силы тренда; падение на высоком объёме — возможно, разворот).\n" +
                "- Любые яркие аномалии или ключевые моменты (резкий скачок цены/объёма).\n\n" +
                "После анализа **дай краткий совет трейдеру**:\n" +
                "- Что можно ожидать в ближайшее время (продолжение тренда, возможный разворот)?\n" +
                "- Выдели ключевые уровни поддержки/сопротивления (если они прослеживаются).\n" +
                "- Рекомендация по действиям (покупать, продавать, держать).\n\n" +
                "Ответ дай на русском языке, чётко и по делу.\n\n" +
                "Данные:\n" + dataCSV;
        return callOpenRouter(prompt);
    }

    private String callOpenRouter(String prompt) {
        var requestBody = new OpenRouterRequest();
        requestBody.setModel(MODEL);
        requestBody.setMessages(List.of(
                new OpenRouterRequest.Message("user", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:25565");
        headers.set("X-Title", "Kripta App");

        HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OpenRouterResponse> response = restTemplate.exchange(
                    OPENROUTER_URL,
                    HttpMethod.POST,
                    entity,
                    OpenRouterResponse.class
            );
            if (response.getBody() != null && response.getBody().getChoices() != null) {
                return response.getBody().getChoices().getFirst().getMessage().getContent();
            }
        } catch (Exception e) {
            log.error("Ошибка вызова OpenRouter", e);
        }
        return "Анализ временно недоступен";
    }

    private int getLimitForPeriod(Period period) {
        int secondsInPeriod = switch (period) {
            case HOUR -> 3600;
            case HOURS_6 -> 6 * 3600;
            case DAY -> 86400;
            case WEEK -> 7 * 86400;
            case MONTH -> 30 * 86400;
        };
        return (int) Math.ceil((double) secondsInPeriod / 20);
    }
}
