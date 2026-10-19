package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class DealLabResultDTO {
    private DealLabCriteriaDTO criteria;
    private int totalEvaluated;
    private int totalCandidates;
    private int totalQualifying;
    private int totalNonQualifying;
    private int totalFailing;

    private List<DealItemDTO> qualifying = new ArrayList<>();
    private List<DealItemDTO> failing = new ArrayList<>();

    private QualifyingProductDTO bestMatchProduct;
    private QualifyingProductDTO cheapestQualifyingProduct;
    private QualifyingProductDTO bestSpecQualifyingProduct;
    private List<QualifyingProductDTO> qualifyingProducts = new ArrayList<>();
    private List<NonQualifyingProductDTO> nonQualifyingProducts = new ArrayList<>();
    private List<RelaxationSuggestionDTO> relaxationSuggestions = new ArrayList<>();
    private String synthesisSummary;

    public DealLabResultDTO() {}

    public DealLabCriteriaDTO getCriteria() { return criteria; }
    public void setCriteria(DealLabCriteriaDTO criteria) { this.criteria = criteria; }

    public int getTotalEvaluated() { return totalEvaluated; }
    public void setTotalEvaluated(int totalEvaluated) {
        this.totalEvaluated = totalEvaluated;
        this.totalCandidates = totalEvaluated;
    }

    public int getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
        this.totalEvaluated = totalCandidates;
    }

    public int getTotalQualifying() { return totalQualifying; }
    public void setTotalQualifying(int totalQualifying) { this.totalQualifying = totalQualifying; }

    public int getTotalNonQualifying() { return totalNonQualifying; }
    public void setTotalNonQualifying(int totalNonQualifying) {
        this.totalNonQualifying = totalNonQualifying;
        this.totalFailing = totalNonQualifying;
    }

    public int getTotalFailing() { return totalFailing; }
    public void setTotalFailing(int totalFailing) {
        this.totalFailing = totalFailing;
        this.totalNonQualifying = totalFailing;
    }

    public List<DealItemDTO> getQualifying() { return qualifying; }
    public void setQualifying(List<DealItemDTO> qualifying) { this.qualifying = qualifying; }

    public List<DealItemDTO> getFailing() { return failing; }
    public void setFailing(List<DealItemDTO> failing) { this.failing = failing; }

    public QualifyingProductDTO getBestMatchProduct() { return bestMatchProduct; }
    public void setBestMatchProduct(QualifyingProductDTO bestMatchProduct) { this.bestMatchProduct = bestMatchProduct; }

    public QualifyingProductDTO getCheapestQualifyingProduct() { return cheapestQualifyingProduct; }
    public void setCheapestQualifyingProduct(QualifyingProductDTO cheapestQualifyingProduct) { this.cheapestQualifyingProduct = cheapestQualifyingProduct; }

    public QualifyingProductDTO getBestSpecQualifyingProduct() { return bestSpecQualifyingProduct; }
    public void setBestSpecQualifyingProduct(QualifyingProductDTO bestSpecQualifyingProduct) { this.bestSpecQualifyingProduct = bestSpecQualifyingProduct; }

    public List<QualifyingProductDTO> getQualifyingProducts() { return qualifyingProducts; }
    public void setQualifyingProducts(List<QualifyingProductDTO> qualifyingProducts) { this.qualifyingProducts = qualifyingProducts; }

    public List<NonQualifyingProductDTO> getNonQualifyingProducts() { return nonQualifyingProducts; }
    public void setNonQualifyingProducts(List<NonQualifyingProductDTO> nonQualifyingProducts) { this.nonQualifyingProducts = nonQualifyingProducts; }

    public List<RelaxationSuggestionDTO> getRelaxationSuggestions() { return relaxationSuggestions; }
    public void setRelaxationSuggestions(List<RelaxationSuggestionDTO> relaxationSuggestions) { this.relaxationSuggestions = relaxationSuggestions; }

    public String getSynthesisSummary() { return synthesisSummary; }
    public void setSynthesisSummary(String synthesisSummary) { this.synthesisSummary = synthesisSummary; }
}
