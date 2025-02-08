package io.github.cpearl0.jasonbot.bot;

import io.github.cpearl0.jasonbot.Config;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatHistory {
    /**
     * @param role "system", "user", "assistant"
     */
    public record ChatMessage(String role, String name, String content) {
    }

    private final Deque<ChatMessage> history = new ConcurrentLinkedDeque<>();
    public final int maxSize;

    public ChatHistory(int maxSize) {
        this.maxSize = maxSize;
    }

    public ChatHistory() {
        this(Config.maxHistorySize);
    }

    public List<ChatMessage> getFullHistory(ServerPlayer player) {
        List<ChatMessage> full = new ArrayList<>();

        var systemMessage = new ChatMessage("system", "system", PromptGenerator.generatePrompt(player));
        full.add(systemMessage); // 保证system始终在首位
        full.addAll(history);
        return full;
    }

    public void addMessage(String role, String name, String content) {
        if (maxSize == 0) {
            if (role.equals("user")) {
                history.clear();
                history.addLast(new ChatMessage(role, name, content));
            }
            return;
        }
        // 自动移除旧消息保持队列长度
        if (history.size() >= maxSize + 1)
            history.removeFirst(); // 移除最旧的消息
        history.addLast(new ChatMessage(role, name, content));
    }

    public void addUserMessage(String name, String content) {
        addMessage("user", name, content);
    }

    public void addSystemMessage(String name, String content) {
        addMessage("system", name, content);
    }

    public void addAssistantMessage(String name, String content) {
        addMessage("assistant", name, content);
    }

    public void clear() {
        history.clear();
    }
}
