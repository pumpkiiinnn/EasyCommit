package com.zoey.easycommit.settings;

import javax.swing.JPanel;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

public class EasyCommitSettingsPanel {
    private final JPanel mainPanel;
    private final JBTextField apiKeyField = new JBTextField();

    public EasyCommitSettingsPanel() {
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("API Key: "), apiKeyField, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String getApiKey() {
        return apiKeyField.getText();
    }

    public void setApiKey(String apiKey) {
        apiKeyField.setText(apiKey);
    }
} 