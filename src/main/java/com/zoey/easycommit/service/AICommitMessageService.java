package com.zoey.easycommit.service;

import com.google.gson.JsonArray;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AICommitMessageService {
    private static final Logger LOG = Logger.getInstance(AICommitMessageService.class);
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String SYSTEM_PROMPT = "你是一个Git提交消息助手。请根据提供的代码变更信息，生成一个简洁明了的提交信息。" +
            "提交信息应该简短（不超过50个字符），并清晰地表达这次变更的主要目的。";

    public String generateCommitMessage(String changes) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + getApiKey());

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "deepseek-chat");
            
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", SYSTEM_PROMPT);

            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", "基于以下变更生成提交信息：\n" + changes);

            JsonArray messages = new JsonArray();
            messages.add(systemMessage);
            messages.add(userMessage);
            
            requestBody.add("messages", messages);
            requestBody.addProperty("stream", false);

            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                return jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            }
        } catch (IOException e) {
            LOG.error("Error calling AI API", e);
            throw new RuntimeException("Failed to generate commit message", e);
        }
    }

    private String getApiKey() {
        return "sk-1ebf04d21dfc415b8f4c1850b816e640";
    }
} 