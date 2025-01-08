package com.zoey.easycommit.action;

import java.util.Collection;

import com.intellij.openapi.vcs.CommitMessageI;
import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ui.CommitMessageProvider;

public class GenerateCommitMessageAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(GenerateCommitMessageAction.class);

    public GenerateCommitMessageAction() {
        super("AI Generate Commit Message", 
              "Generate commit message using AI",
              AllIcons.Actions.Lightning);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.warn("Action performed called");
        
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        LOG.warn("Project: " + project.getName());

        try {
            // 获取当前的变更列表
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            Collection<Change> changes = changeListManager.getDefaultChangeList().getChanges();
            LOG.warn("Changes size: " + changes.size());

            if (changes.isEmpty()) {
                Messages.showWarningDialog(project, "No changes to commit", "Generate Commit Message");
                return;
            }

            // 尝试获取提交消息控件
            CommitMessageI commitPanel = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
            if (commitPanel != null) {
                String generatedMessage = "feat: temporary commit message";
                commitPanel.setCommitMessage(generatedMessage);
                LOG.warn("Message set successfully");
                Messages.showInfoMessage(project, "Commit message generated successfully!", "Generate Commit Message");
            } else {
                LOG.error("Could not find commit panel");
                Messages.showErrorDialog(project, 
                    "Could not access commit message panel. Please make sure you're in the commit dialog.", 
                    "Generate Commit Message");
            }
        } catch (Exception ex) {
            LOG.error("Error in actionPerformed", ex);
            Messages.showErrorDialog(project, 
                "Error: " + ex.getMessage(), 
                "Generate Commit Message Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        try {
            Project project = e.getProject();
            if (project == null) {
                LOG.warn("Project is null in update");
                e.getPresentation().setEnabled(false);
                return;
            }

            // 检查是否有未提交的更改
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            boolean hasChanges = !changeListManager.getDefaultChangeList().getChanges().isEmpty();
            
            // 检查是否在提交对话框中
            CommitMessageI commitPanel = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
            boolean hasCommitPanel = commitPanel != null;
            
            boolean isEnabled = hasChanges && hasCommitPanel;
            LOG.warn("Action enabled: " + isEnabled + " (hasChanges: " + hasChanges + ", hasCommitPanel: " + hasCommitPanel + ")");
            
            e.getPresentation().setEnabled(isEnabled);
            e.getPresentation().setVisible(true);
            
        } catch (Exception ex) {
            LOG.error("Error in update", ex);
            e.getPresentation().setEnabled(false);
        }
    }
} 