package com.zoey.easycommit.service.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zoey.easycommit.settings.EasyCommitSettings;

public class AIPrompts {
    public static final String SYSTEM_PROMPT =
            """
                    你是一个专业的 Git 提交消息助手。你的目标是根据提供的代码变更信息，生成高质量且符合规范的 Git commit 消息。
                    
                    生成 commit 消息需满足以下条件：
                    
                    1. <style>。
                    2. **长度限制**：首行长度不超过 <length> 个字符。
                    3. **清晰表达意图**：准确概括本次代码变更的主要目的和意图。
                    4. **使用<language>**：commit 消息必须使用<language>书写。
                    5. **遵循约定**：建议采用 `<类型>[（可选的作用域）]: <描述>` 的格式，例如：`feat: 添加用户认证功能`，`fix(auth): 修复登录失败的 bug`。 常见的类型包括 `feat` (新功能), `fix` (修复 bug), `docs` (文档变更), `style` (代码格式调整，不影响逻辑), `refactor` (重构，不涉及功能变更), `perf` (性能优化), `test` (添加或修改测试), `build` (构建流程或依赖项变更), `ci` (CI 配置变更), `chore` (不属于以上类型的其他变更)。
                    
                    输出格式要求：
                    
                    请将生成的 commit 消息以标准的 JSON 格式输出，键名为 "commit_message"。**最终输出必须是纯粹的 JSON 内容，请勿添加任何额外的文字或描述。**
                    
                    输出示例：
                    
                    {"commit_message": "重构：优化数据处理逻辑"}
                    
                    """;


    public static JsonArray createMessages(String changes, EasyCommitSettings settings) {
        JsonArray messages = new JsonArray();

        String messageStyle = settings.getMessageStyle();
        String lengthLimit,style;

        // 根据messageStyle设置不同的字符长度限制
        if ("professional".equals(messageStyle)) {
            lengthLimit = "300";
            style = "**内容专业**：把修改内容详细切专业的全部描述出来";
        } else {
            lengthLimit = "50";
            style = "**简洁明了**：核心信息一目了然，避免冗余描述";
        }

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", SYSTEM_PROMPT
                .replace("<language>", settings.getLanguage())
                .replace("<length>", lengthLimit)
                .replace("<style>", style));

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Based on the following changes, generate a commit message:\n" + changes);

        messages.add(systemMessage);
        messages.add(userMessage);
        return messages;
    }
} 