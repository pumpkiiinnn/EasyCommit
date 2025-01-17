package com.zoey.easycommit.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.FormBuilder;
import com.zoey.easycommit.service.ai.AIModelType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EasyCommitSettingsPanel {
    private final JPanel myMainPanel;
    private final JBPasswordField apiKeyField;
    private final ComboBox<AIModelType> modelComboBox;
    private ComboBox<String> languageComboBox;
    private ComboBox<String> styleComboBox;
    private JCheckBox useEmojiCheckBox;
    private JLabel emojiLinkLabel;

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

        // 初始化新组件
        initializeNewComponents();

        // 创建分隔线
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(1, 10));

//        boolean isChinese = Locale.getDefault().getLanguage().equals("zh");

        JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // 将组件添加到水平面板
        emojiPanel.add(useEmojiCheckBox);
        emojiPanel.add(emojiLinkLabel);

        // 使用 FormBuilder 创建布局
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("AI Model: "), modelComboBox)
                .addLabeledComponent(new JBLabel("API Key: "), apiKeyField)
                .addComponent(separator)
                .addSeparator()
                .addLabeledComponent(new JBLabel("Language: "), languageComboBox)
                .addLabeledComponent(new JBLabel("Message Style: "), styleComboBox)
                .addComponent(emojiPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private void initializeNewComponents() {
//        boolean isChinese = Locale.getDefault().getLanguage().equals("zh");

        // 语言选择
        languageComboBox = new ComboBox<>();
        for (EasyCommitSettings.Language lang : EasyCommitSettings.Language.values()) {
            languageComboBox.addItem(lang.getDisplay());
        }

        // 风格选择
        styleComboBox = new ComboBox<>();
        for (EasyCommitSettings.MessageStyle style : EasyCommitSettings.MessageStyle.values()) {
            styleComboBox.addItem(style.getDisplay(false));
        }

        // 创建水平面板用于放置 Emoji 相关组件
        JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // Emoji 选择
        useEmojiCheckBox = new JCheckBox("Use Emoji");

        // Emoji 链接图标
        emojiLinkLabel = new JLabel("<html><a href='https://gitmoji.dev'>Help with Gitmoji Guide</a></html>");
        emojiLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emojiLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse("https://gitmoji.dev");
            }
        });

        // 将组件添加到水平面板
        emojiPanel.add(useEmojiCheckBox);
        emojiPanel.add(emojiLinkLabel);
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
                !modelComboBox.getSelectedItem().equals(settings.getSelectedModel()) ||
                !settings.getLanguage().equals(getSelectedLanguageCode()) ||
                !settings.getMessageStyle().equals(getSelectedStyleCode()) ||
                settings.isUseEmoji() != useEmojiCheckBox.isSelected();
    }

    public void apply(EasyCommitSettings settings) {
        settings.setApiKey(String.valueOf(apiKeyField.getPassword()));
        settings.setSelectedModel((AIModelType) modelComboBox.getSelectedItem());
        settings.setLanguage(getSelectedLanguageCode());
        settings.setMessageStyle(getSelectedStyleCode());
        settings.setUseEmoji(useEmojiCheckBox.isSelected());
    }

    public void reset(EasyCommitSettings settings) {
        apiKeyField.setText(settings.getApiKey());
        modelComboBox.setSelectedItem(settings.getSelectedModel());
        setSelectedLanguage(settings.getLanguage());
        setSelectedStyle(settings.getMessageStyle());
        useEmojiCheckBox.setSelected(settings.isUseEmoji());
    }

    private String getSelectedLanguageCode() {
        int index = languageComboBox.getSelectedIndex();
        return EasyCommitSettings.Language.values()[index].getCode();
    }

    private String getSelectedStyleCode() {
        int index = styleComboBox.getSelectedIndex();
        return EasyCommitSettings.MessageStyle.values()[index].getCode();
    }

    private void setSelectedLanguage(String code) {
        for (int i = 0; i < EasyCommitSettings.Language.values().length; i++) {
            if (EasyCommitSettings.Language.values()[i].getCode().equals(code)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void setSelectedStyle(String code) {
        for (int i = 0; i < EasyCommitSettings.MessageStyle.values().length; i++) {
            if (EasyCommitSettings.MessageStyle.values()[i].getCode().equals(code)) {
                styleComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
} 