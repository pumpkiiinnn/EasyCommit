package com.zoey.easycommit.service.ai;

import com.zoey.easycommit.settings.EasyCommitSettings;

public interface AIProvider {
    String generateCommitMessage(String changes, EasyCommitSettings settings);

    boolean isConfigured();
}