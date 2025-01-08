package com.zoey.easycommit;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

public class SettingEasyCommit extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() != null) {
            ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), "Easy Commit");
        }
    }
}
