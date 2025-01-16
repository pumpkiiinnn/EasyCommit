package com.zoey.easycommit.action;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
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
import com.zoey.easycommit.service.AICommitMessageService;
import org.apache.commons.lang3.StringUtils;

public class GenerateCommitMessageAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(GenerateCommitMessageAction.class);
    private final AICommitMessageService aiService = new AICommitMessageService();
    private static final int MAX_CONTENT_LENGTH = 500; // 每个文件最大内容长度
    private static final int MAX_TOTAL_LENGTH = 4000; // 总内容最大长度
    private static final Set<String> BINARY_FILE_EXTENSIONS = Set.of(
        "png", "jpg", "jpeg", "gif", "ico", "jar", "zip", "class"
    );

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
            String aiGeneratedMessage = aiService.generateCommitMessage(buildChangeDescription(changes));
            
            // 尝试获取提交消息控件
            CommitMessageI commitPanel = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
            if (commitPanel != null) {
                commitPanel.setCommitMessage(aiGeneratedMessage);
                LOG.warn("Message set successfully");
            }
        } catch (Exception ex) {
            LOG.error("Error in actionPerformed", ex);
            Messages.showErrorDialog(project, 
                "Error: " + ex.getMessage(), 
                "Generate Commit Message Error");
        }
    }

    private String buildChangeDescription(Collection<Change> changes) {
        StringBuilder description = new StringBuilder("Changes:\n\n");
        int totalLength = 0;

        for (Change change : changes) {
            if (totalLength >= MAX_TOTAL_LENGTH) {
                description.append("...(more changes omitted)\n");
                break;
            }

            String filePath;
        if (change.getAfterRevision() != null) {
            filePath = change.getAfterRevision().getFile().getPath();
        } else if (change.getBeforeRevision() != null) {
            filePath = change.getBeforeRevision().getFile().getPath();
        } else {
            filePath = "Unknown file path";
        }
            String changeType = getChangeType(change);
            String fileExtension = getFileExtension(filePath);

            description.append(changeType).append(": ").append(filePath).append("\n");

            // 跳过二进制文件的内容比较
            if (BINARY_FILE_EXTENSIONS.contains(fileExtension)) {
                description.append("(Binary file)\n\n");
                continue;
            }

            try {
                if (change.getBeforeRevision() != null && change.getAfterRevision() != null) {
                    String beforeContent = change.getBeforeRevision().getContent();
                    String afterContent = change.getAfterRevision().getContent();
                    
                    if (beforeContent != null && afterContent != null) {
                        String diff = generateDiff(beforeContent, afterContent);
                        if (!diff.isEmpty()) {
                            description.append("Changes:\n").append(diff).append("\n");
                        }
                    }
                } else if (change.getAfterRevision() != null) {
                    // 新增文件
                    String content = change.getAfterRevision().getContent();
                    if (content != null) {
                        description.append("New content:\n")
                                 .append(truncateContent(content))
                                 .append("\n");
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error processing file content: " + filePath, e);
                description.append("(Content processing error)\n");
            }
            
            description.append("\n");
            totalLength = description.length();
        }

        return description.toString();
    }

    private String generateDiff(String beforeContent, String afterContent) {
        // 使用简单的行比较来生成差异
        List<String> beforeLines = Arrays.asList(beforeContent.split("\n"));
        List<String> afterLines = Arrays.asList(afterContent.split("\n"));
        
        StringBuilder diff = new StringBuilder();
        int contextLines = 3; // 显示变更周围的行数
        
        try {
            // 使用Myers差异算法计算变更
            Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);
            
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                int startOriginal = Math.max(0, delta.getSource().getPosition() - contextLines);
                int endOriginal = Math.min(beforeLines.size(), 
                    delta.getSource().getPosition() + delta.getSource().size() + contextLines);
                
                // 添加上下文和变更信息
                for (int i = startOriginal; i < endOriginal; i++) {
                    if (i < delta.getSource().getPosition() || 
                        i >= delta.getSource().getPosition() + delta.getSource().size()) {
                        // 上下文行
                        diff.append("  ").append(beforeLines.get(i)).append("\n");
                    } else if (delta.getType() == DeltaType.DELETE || delta.getType() == DeltaType.CHANGE) {
                        // 删除的行
                        diff.append("- ").append(beforeLines.get(i)).append("\n");
                    }
                }
                
                // 添加新增的行
                if (delta.getType() == DeltaType.INSERT || delta.getType() == DeltaType.CHANGE) {
                    for (String line : delta.getTarget().getLines()) {
                        diff.append("+ ").append(line).append("\n");
                    }
                }
            }
            
            return truncateContent(diff.toString());
        } catch (Exception e) {
            LOG.warn("Error generating diff", e);
            return "(Error generating diff)";
        }
    }

    private String truncateContent(String content) {
        if (content.length() <= MAX_CONTENT_LENGTH) {
            return content;
        }
        
        // 尝试在一个完整的行结束处截断
        int endIndex = content.lastIndexOf('\n', MAX_CONTENT_LENGTH);
        if (endIndex == -1) {
            endIndex = MAX_CONTENT_LENGTH;
        }
        
        return content.substring(0, endIndex) + "\n...(content truncated)\n";
    }

    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return dotIndex > 0 ? filePath.substring(dotIndex + 1).toLowerCase() : "";
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