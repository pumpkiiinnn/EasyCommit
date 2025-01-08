package com.zoey.easycommit.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
        name = "com.zoey.easycommit.settings.EasyCommitSettings",
        storages = @Storage("EasyCommitSettings.xml")
)
@Service
public class EasyCommitSettings implements PersistentStateComponent<EasyCommitSettings> {
    private String apiKey = "";

    public static EasyCommitSettings getInstance() {
        return ServiceManager.getService(EasyCommitSettings.class);
    }

    @Nullable
    @Override
    public EasyCommitSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EasyCommitSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
} 