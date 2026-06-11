package com.oms.service;

import com.oms.dto.MlAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MlService {

    private final WebClient mlWebClient;

    public MlAnalysisResult analyze(String text) {
        try {
            return mlWebClient.post()
                    .uri("/api/ml/analyze")
                    .bodyValue(Map.of("text", text, "source", "java-backend"))
                    .retrieve()
                    .bodyToMono(MlAnalysisResult.class)
                    .block();
        } catch (Exception e) {
            log.error("ML service call failed: {}", e.getMessage());
            return fallbackResult(text);
        }
    }

    // Rule-based fallback when the Python service is unavailable
    private MlAnalysisResult fallbackResult(String text) {
        log.warn("Using rule-based fallback for text analysis");
        String lower = text.toLowerCase();
        boolean offensive = lower.matches(".*(hate|kill|die|stupid|idiot|loser|ugly|freak|dumb|worthless).*");

        MlAnalysisResult result = new MlAnalysisResult();
        result.setLabel(offensive ? "offensive" : "normal");
        result.setOffensive(offensive);
        result.setConfidence(offensive ? 0.65 : 0.70);
        result.setSeverity(offensive ? "medium" : "none");
        result.setOriginalText(text);
        return result;
    }
}
