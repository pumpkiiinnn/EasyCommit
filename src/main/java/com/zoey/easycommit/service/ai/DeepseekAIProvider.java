package com.zoey.easycommit.service.ai;

import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class DeepseekAIProvider implements AIProvider {
    private static final Logger LOG = Logger.getInstance(DeepseekAIProvider.class);
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private final String apiKey;

    public DeepseekAIProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String generateCommitMessage(String changes) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "deepseek-chat");
            
            JsonArray messages = createMessages(changes);
            requestBody.add("messages", messages);
            requestBody.addProperty("stream", false);

            Log.info("Request body: " + requestBody);
            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                return jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            }
        } catch (Exception e) {
            LOG.error("Error calling Deepseek API", e);
            throw new RuntimeException("Failed to generate commit message", e);
        }
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
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