package com.zoey.easycommit.settings;

import javax.swing.JComponent;

import org.jetbrains.annotations.Nls;

import com.intellij.openapi.options.Configurable;

public class EasyCommitConfigurable implements Configurable {
    private EasyCommitSettingsPanel settingsPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Easy Commit";
    }

    @Override
    public JComponent createComponent() {
        settingsPanel = new EasyCommitSettingsPanel();
        return settingsPanel.getMainPanel();
    }

    @Override
    public boolean isModified() {
        EasyCommitSettings settings = EasyCommitSettings.getInstance();
        return !settingsPanel.getApiKey().equals(settings.getApiKey());
    }

    @Override
    public void apply() {
        EasyCommitSettings settings = EasyCommitSettings.getInstance();
        settings.setApiKey(settingsPanel.getApiKey());
    }

    @Override
    public void reset() {
        EasyCommitSettings settings = EasyCommitSettings.getInstance();
        settingsPanel.setApiKey(settings.getApiKey());
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
} 