package com.zoey.easycommit.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EasyCommitConfigurable implements Configurable {
    private EasyCommitSettingsPanel mySettingsPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Easy Commit";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsPanel.getModelComboBox();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsPanel = new EasyCommitSettingsPanel();
        return mySettingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return mySettingsPanel.isModified(EasyCommitSettings.getInstance());
    }

    @Override
    public void apply() {
        mySettingsPanel.apply(EasyCommitSettings.getInstance());
    }

    @Override
    public void reset() {
        mySettingsPanel.reset(EasyCommitSettings.getInstance());
    }

    @Override
    public void disposeUIResources() {
        mySettingsPanel = null;
    }
} 