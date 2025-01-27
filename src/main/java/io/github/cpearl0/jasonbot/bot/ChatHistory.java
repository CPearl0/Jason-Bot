package io.github.cpearl0.jasonbot.bot;

import io.github.cpearl0.jasonbot.Config;

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

    private ChatMessage systemMessage;
    private final Deque<ChatMessage> history = new ConcurrentLinkedDeque<>();
    public final int maxSize;

    public ChatHistory(int maxSize) {
        this.maxSize = maxSize;
        initializeSystemMessage();
    }

    public ChatHistory() {
        this(Config.maxHistorySize);
    }

    private void initializeSystemMessage() {
        systemMessage = new ChatMessage("system", "system", Config.systemPrompt);
    }

    public List<ChatMessage> getFullHistory() {
        List<ChatMessage> full = new ArrayList<>();
        full.add(systemMessage); // 保证system始终在首位
        full.addAll(history);
        return full;
    }

    public void addMessage(String role, String name, String content) {
        // 自动移除旧消息保持队列长度
        if (history.size() >= maxSize) {
            history.removeFirst(); // 移除最旧的消息
        }
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
