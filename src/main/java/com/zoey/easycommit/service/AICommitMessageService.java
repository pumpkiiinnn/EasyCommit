package com.zoey.easycommit.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.diagnostic.Logger;
import com.zoey.easycommit.service.ai.AIModelType;
import com.zoey.easycommit.service.ai.AIProvider;
import com.zoey.easycommit.service.ai.AIProviderFactory;
import com.zoey.easycommit.settings.EasyCommitSettings;

public class AICommitMessageService {
    private static final Logger LOG = Logger.getInstance(AICommitMessageService.class);

    public String generateCommitMessage(String changes) {
        EasyCommitSettings settings = EasyCommitSettings.getInstance();
        AIModelType modelType = settings.getSelectedModel();
        String apiKey = settings.getApiKey();

        AIProvider provider = AIProviderFactory.createProvider(modelType, apiKey);
        
        if (!provider.isConfigured()) {
            throw new RuntimeException("AI provider is not properly configured. Please check your settings.");
        }

        String response = provider.generateCommitMessage(changes);
        
        // 清理响应中可能存在的语言标记
        response = response.replaceAll("^\\s*```\\w*\\s*", "").replaceAll("\\s*```\\s*$", "");
        
        try {
            // 解析JSON响应
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.get("commit_message").getAsString();
        } catch (Exception e) {
            LOG.error("Error parsing AI response", e);
            throw new RuntimeException("Failed to parse AI response: " + response, e);
        }
    }
} 