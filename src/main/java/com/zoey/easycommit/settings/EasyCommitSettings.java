package com.zoey.easycommit.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.zoey.easycommit.service.ai.AIModelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "com.zoey.easycommit.settings.EasyCommitSettings",
    storages = @Storage("EasyCommitSettings.xml")
)
public class EasyCommitSettings implements PersistentStateComponent<EasyCommitSettings.State> {
    private State myState = new State();

    public static class State {
        public String apiKey = "";
        public AIModelType selectedModel = AIModelType.DEEPSEEK;
    }

    public static EasyCommitSettings getInstance() {
        return ApplicationManager.getApplication().getService(EasyCommitSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public String getApiKey() {
        return myState.apiKey;
    }

    public void setApiKey(String apiKey) {
        myState.apiKey = apiKey;
    }

    public AIModelType getSelectedModel() {
        return myState.selectedModel;
    }

    public void setSelectedModel(AIModelType model) {
        myState.selectedModel = model;
    }
} 