package com.zoey.easycommit.service;

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

        return provider.generateCommitMessage(changes);
    }
} 