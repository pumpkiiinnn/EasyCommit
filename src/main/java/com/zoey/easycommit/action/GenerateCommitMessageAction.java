package com.zoey.easycommit.action;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;

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

            // 构建变更信息
            StringBuilder commitMessage = new StringBuilder("Changes:\n\n");
            for (Change change : changes) {
                String filePath = change.getVirtualFile().getPath();
                String changeType = getChangeType(change);
                
                commitMessage.append(changeType).append(": ").append(filePath).append("\n");
                
                // 获取具体的内容变更
                if (change.getBeforeRevision() != null && change.getAfterRevision() != null) {
                    String beforeContent = change.getBeforeRevision().getContent();
                    String afterContent = change.getAfterRevision().getContent();
                    if (beforeContent != null && afterContent != null) {
                        commitMessage.append("Modified content:\n");
                        commitMessage.append("Before: ").append(beforeContent.substring(0, Math.min(100, beforeContent.length()))).append("...\n");
                        commitMessage.append("After: ").append(afterContent.substring(0, Math.min(100, afterContent.length()))).append("...\n");
                    }
                }
                commitMessage.append("\n");
            }

            // 尝试获取提交消息控件
            CommitMessageI commitPanel = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
            if (commitPanel != null) {
                commitPanel.setCommitMessage(commitMessage.toString());
                LOG.warn("Message set successfully");
                // 暂时不需要弹窗提示
//                Messages.showInfoMessage(project, "Commit message generated successfully!", "Generate Commit Message");
            } else {
                LOG.error("Could not find commit panel");
                Messages.showErrorDialog(project, "Could not access commit message panel. Please make sure you're in the commit dialog.", "Generate Commit Message");
            }
        } catch (Exception ex) {
            LOG.error("Error in actionPerformed", ex);
            Messages.showErrorDialog(project, 
                "Error: " + ex.getMessage(), 
                "Generate Commit Message Error");
        }
    }

    private String getChangeType(Change change) {
        if (change.getBeforeRevision() == null && change.getAfterRevision() != null) {
            return "Added";
        } else if (change.getBeforeRevision() != null && change.getAfterRevision() == null) {
            return "Deleted";
        } else {
            return "Modified";
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