package com.zoey.easycommit.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.FormBuilder;
import com.zoey.easycommit.service.ai.AIModelType;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

public class EasyCommitSettingsPanel {
    private final JPanel myMainPanel;
    private final JBPasswordField apiKeyField;
    private final ComboBox<AIModelType> modelComboBox;

    public EasyCommitSettingsPanel() {
        apiKeyField = new JBPasswordField();
        modelComboBox = new ComboBox<>(AIModelType.values());

        // 设置下拉框显示名称
        modelComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AIModelType) {
                    setText(((AIModelType) value).getDisplayName());
                }
                return this;
            }
        });

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("AI Model: "), modelComboBox)
                .addLabeledComponent(new JBLabel("API Key: "), apiKeyField)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JBPasswordField getApiKeyField() {
        return apiKeyField;
    }

    public JComboBox<AIModelType> getModelComboBox() {
        return modelComboBox;
    }

    public boolean isModified(EasyCommitSettings settings) {
        return !String.valueOf(apiKeyField.getPassword()).equals(settings.getApiKey()) ||
                !modelComboBox.getSelectedItem().equals(settings.getSelectedModel());
    }

    public void apply(EasyCommitSettings settings) {
        settings.setApiKey(String.valueOf(apiKeyField.getPassword()));
        settings.setSelectedModel((AIModelType) modelComboBox.getSelectedItem());
    }

    public void reset(EasyCommitSettings settings) {
        apiKeyField.setText(settings.getApiKey());
        modelComboBox.setSelectedItem(settings.getSelectedModel());
    }
} 