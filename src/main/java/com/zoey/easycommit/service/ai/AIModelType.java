package com.zoey.easycommit.service.ai;

public enum AIModelType {
    DEEPSEEK("Deepseek"),
    OPENAI("OpenAI"),
    CLAUDE("Claude");

    private final String displayName;

    AIModelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 