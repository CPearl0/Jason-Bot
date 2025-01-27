package io.github.cpearl0.jasonbot.bot;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import io.github.cpearl0.jasonbot.Config;
import io.github.cpearl0.jasonbot.JasonBot;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class AsyncDeepSeekChat {
    private static final String API_ENDPOINT = "https://api.deepseek.com/v1/chat/completions";

    private static ExecutorService executor;
    private static final Gson gson = new Gson();

    public static FakePlayer jasonBotPlayer;

    public static final ChatHistory chatHistory = new ChatHistory();

    public static void start(MinecraftServer server) {
        if (executor == null || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        jasonBotPlayer = new FakePlayer(server.overworld(), new GameProfile(UUID.randomUUID(), Config.assistantName));
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
        chatHistory.clear();
    }

    public static CompletableFuture<String> chatAsync(String userName, String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建请求体
                JsonObject requestBody = getRequestBody(userName, userMessage);

                // 发送请求
                HttpURLConnection connection = createConnection();
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8));
                }

                // 处理响应
                return parseResponse(connection);
            } catch (Exception e) {
                throw new CompletionException("API Failed", e);
            }
        }, executor);
    }

    private static @NotNull JsonObject getRequestBody(String userName, String userMessage) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", Config.DeepSeekModel);
        requestBody.addProperty("temperature", Config.temperature);

        chatHistory.addUserMessage(userName, userMessage);

        JsonArray messages = gson.toJsonTree(chatHistory.getFullHistory()).getAsJsonArray();

        requestBody.add("messages", messages);
        return requestBody;
    }

    private static HttpURLConnection createConnection() throws IOException {
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + Config.DeepSeekAPIKey);
        connection.setDoOutput(true);
        return connection;
    }

    private static String parseResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            JsonObject response = gson.fromJson(reader, JsonObject.class);
            return response.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }

    public static void chat(String userName, String userMessage, Consumer<String> responseHandler) {
        chatAsync(userName, userMessage)
                .thenAccept(response -> {
                    responseHandler.accept(response);
                    chatHistory.addAssistantMessage(jasonBotPlayer.getDisplayName().getString(), response);
                })
                .exceptionally(ex -> {
                    JasonBot.LOGGER.error(ex.getCause().getMessage());
                    return null;
                });
    }
}