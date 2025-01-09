package com.zoey.easycommit.service.ai;

public interface AIProvider {
    String generateCommitMessage(String changes);
    boolean isConfigured();
} 