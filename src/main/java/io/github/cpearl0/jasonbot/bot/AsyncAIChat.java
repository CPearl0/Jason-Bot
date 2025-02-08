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
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class AsyncAIChat {
    private static final String API_ENDPOINT = Config.APIEndpoint;

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

    public static CompletableFuture<String> chatAsync(ServerPlayer player, String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建请求体
                JsonObject requestBody = getRequestBody(player, userMessage);

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

    private static @NotNull JsonObject getRequestBody(ServerPlayer player, String userMessage) {
        var userName = player.getDisplayName().getString();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", Config.AIModel);
        requestBody.addProperty("temperature", Config.temperature);
        requestBody.addProperty("presence_penalty", Config.presencePenalty);

        chatHistory.addUserMessage(userName, userMessage);

        JsonArray messages = gson.toJsonTree(chatHistory.getFullHistory(player)).getAsJsonArray();

        requestBody.add("messages", messages);
        return requestBody;
    }

    private static HttpURLConnection createConnection() throws IOException {
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + Config.APIKey);
        connection.setDoOutput(true);
        return connection;
    }

    private static String parseResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            JsonObject response = gson.fromJson(reader, JsonObject.class);
            if (response == null)
                return Component.translatable("info.jasonbot.APIfailed").getString();

            return response.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }

    public static void chat(ServerPlayer player, String userMessage, Consumer<String> responseHandler) {
        chatAsync(player, userMessage)
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