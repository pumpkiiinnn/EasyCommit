package com.zoey.easycommit.service.ai;

public enum AIModelType {
    DEEPSEEK("Deepseek"),
    OPENAI("OpenAI"),
    CLAUDE("Claude"),
    AI_IA("AI-IA");

    private final String displayName;

    AIModelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 