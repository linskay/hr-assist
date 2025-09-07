package com.example.hr_assistant.model;

public enum RecommendationStatus {
    PROCEED_TO_NEXT_STEP("На следующий этап"),
    REJECT("Отказ"),
    NEEDS_FURTHER_REVIEW("Требуется уточнение");

    private final String description;

    RecommendationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
