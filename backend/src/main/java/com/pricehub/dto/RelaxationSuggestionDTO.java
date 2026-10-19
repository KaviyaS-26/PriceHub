package com.pricehub.dto;

public class RelaxationSuggestionDTO {
    private String constraint; // "maxBudget", "minRating", "minWarrantyMonths"
    private String constraintType; // "BUDGET", "RATING", "WARRANTY"
    private String suggestionText; // "Relaxing warranty requirement unlocks 2 more deals"
    private Integer additionalProductsUnlocked;
    private String actionLabel; // "Remove warranty filter"
    private String simulatedCriteriaChange;

    public RelaxationSuggestionDTO() {}

    public RelaxationSuggestionDTO(String constraintType, String suggestionText, Integer additionalProductsUnlocked, String simulatedCriteriaChange) {
        this.constraintType = constraintType;
        this.suggestionText = suggestionText;
        this.additionalProductsUnlocked = additionalProductsUnlocked;
        this.simulatedCriteriaChange = simulatedCriteriaChange;
    }

    public RelaxationSuggestionDTO(String constraint, String constraintType, String suggestionText, Integer additionalProductsUnlocked, String actionLabel) {
        this.constraint = constraint;
        this.constraintType = constraintType;
        this.suggestionText = suggestionText;
        this.additionalProductsUnlocked = additionalProductsUnlocked;
        this.actionLabel = actionLabel;
        this.simulatedCriteriaChange = actionLabel;
    }

    public String getConstraint() { return constraint; }
    public void setConstraint(String constraint) { this.constraint = constraint; }

    public String getConstraintType() { return constraintType; }
    public void setConstraintType(String constraintType) { this.constraintType = constraintType; }

    public String getSuggestionText() { return suggestionText; }
    public void setSuggestionText(String suggestionText) { this.suggestionText = suggestionText; }

    public Integer getAdditionalProductsUnlocked() { return additionalProductsUnlocked; }
    public void setAdditionalProductsUnlocked(Integer additionalProductsUnlocked) { this.additionalProductsUnlocked = additionalProductsUnlocked; }

    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }

    public String getSimulatedCriteriaChange() { return simulatedCriteriaChange; }
    public void setSimulatedCriteriaChange(String simulatedCriteriaChange) { this.simulatedCriteriaChange = simulatedCriteriaChange; }
}
