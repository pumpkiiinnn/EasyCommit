package com.zoey.easycommit.service.ai;

public class AIProviderFactory {
    public static AIProvider createProvider(AIModelType type, String apiKey) {
        return switch (type) {
            case DEEPSEEK -> new DeepseekAIProvider(apiKey);
            case OPENAI -> new OpenAIProvider(apiKey);
            case CLAUDE -> new ClaudeAIProvider(apiKey);
            default ->  new MyLLMProvider();
        };
    }
} 