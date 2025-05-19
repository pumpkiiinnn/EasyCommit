package com.zoey.easycommit.service.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import com.zoey.easycommit.settings.EasyCommitSettings;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class MyLLMProvider implements AIProvider {
    private static final Logger LOG = Logger.getInstance(MyLLMProvider.class);
    private static final String API_URL = "https://myllm.ai-ia.cc/api/langchain/completions";

    public MyLLMProvider() {
    }

    @Override
    public String generateCommitMessage(String changes, EasyCommitSettings settings) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");

            // 新的请求格式
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("application", "ezcommit");

            // 创建提示信息
            requestBody.addProperty("message", AIPrompts.createMessages(changes, settings).toString());

            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);

                // 假设返回的格式包含 content 字段
                return jsonResponse.get("message").getAsString();
            }
        } catch (Exception e) {
            LOG.error("Error calling MyLLM API", e);
            throw new RuntimeException("Failed to generate commit message", e);
        }
    }

    @Override
    public boolean isConfigured() {
        // 不需要API密钥，所以总是配置好的
        return true;
    }

    private JsonArray createMessages(String changes) {
        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", AIPrompts.SYSTEM_PROMPT);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "基于以下变更生成提交信息：\n" + changes);

        messages.add(systemMessage);
        messages.add(userMessage);
        return messages;
    }
} 