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
public class EasyCommitSettings implements PersistentStateComponent<EasyCommitSettings> {
    private State myState = new State();
    private String language = "en"; // 默认英语
    private String messageStyle = "professional"; // 默认专业风格
    private boolean useEmoji = false; // 默认不使用emoji

    public static class State {
        public String apiKey = "";
        public AIModelType selectedModel = AIModelType.DEEPSEEK;
    }

    public static EasyCommitSettings getInstance() {
        return ApplicationManager.getApplication().getService(EasyCommitSettings.class);
    }

    @Nullable
    @Override
    public EasyCommitSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EasyCommitSettings state) {
        this.myState = state.myState;
        this.language = state.language;
        this.messageStyle = state.messageStyle;
        this.useEmoji = state.useEmoji;
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

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getMessageStyle() { return messageStyle; }
    public void setMessageStyle(String messageStyle) { this.messageStyle = messageStyle; }

    public boolean isUseEmoji() { return useEmoji; }
    public void setUseEmoji(boolean useEmoji) { this.useEmoji = useEmoji; }

    // 语言选项枚举
    public enum Language {
        ENGLISH("en", "English"),
        CHINESE("zh", "中文"),
        JAPANESE("ja", "日本語"),
        FRENCH("fr", "Français");
        
        private final String code;
        private final String display;
        
        Language(String code, String display) {
            this.code = code;
            this.display = display;
        }
        
        public String getCode() { return code; }
        public String getDisplay() { return display; }
    }
    
    // 风格选项枚举
    public enum MessageStyle {
        PROFESSIONAL("professional", "Professional", "专业"),
        SIMPLE("simple", "Simple", "简单");
        
        private final String code;
        private final String displayEn;
        private final String displayZh;
        
        MessageStyle(String code, String displayEn, String displayZh) {
            this.code = code;
            this.displayEn = displayEn;
            this.displayZh = displayZh;
        }
        
        public String getCode() { return code; }
        public String getDisplay(boolean isChinese) {
            return isChinese ? displayZh : displayEn;
        }
    }
} 