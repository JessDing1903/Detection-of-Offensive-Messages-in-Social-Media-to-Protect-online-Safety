package com.oms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MlAnalysisResult {
    private String label;

    @JsonProperty("is_offensive")
    private boolean isOffensive;

    private double confidence;
    private String severity;

    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("analyzed_at")
    private String analyzedAt;
}
