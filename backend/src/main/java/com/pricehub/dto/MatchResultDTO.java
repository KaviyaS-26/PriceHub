package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchResultDTO {
    private MatchConfidence confidence;
    private Double matchScore; // 0.0 to 100.0
    private String reason;
    private List<String> matchingAttributes = new ArrayList<>();
    private List<String> differingAttributes = new ArrayList<>();
    private boolean isCriticalMismatch;

    public MatchResultDTO() {}

    public MatchResultDTO(MatchConfidence confidence, Double matchScore, String reason) {
        this.confidence = confidence;
        this.matchScore = matchScore;
        this.reason = reason;
    }

    public MatchConfidence getConfidence() { return confidence; }
    public void setConfidence(MatchConfidence confidence) { this.confidence = confidence; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<String> getMatchingAttributes() { return matchingAttributes; }
    public void setMatchingAttributes(List<String> matchingAttributes) { this.matchingAttributes = matchingAttributes; }

    public List<String> getDifferingAttributes() { return differingAttributes; }
    public void setDifferingAttributes(List<String> differingAttributes) { this.differingAttributes = differingAttributes; }

    public boolean isCriticalMismatch() { return isCriticalMismatch; }
    public void setCriticalMismatch(boolean criticalMismatch) { isCriticalMismatch = criticalMismatch; }
}
