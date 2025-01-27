package io.github.cpearl0.jasonbot.event;

import io.github.cpearl0.jasonbot.Config;
import io.github.cpearl0.jasonbot.JasonBot;
import io.github.cpearl0.jasonbot.bot.AsyncDeepSeekChat;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JasonBot.MODID)
public class EventHandler {
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String message = event.getMessage().getString();
        boolean wake = Config.wakeNames.stream().anyMatch(name -> message.toLowerCase().startsWith(name.toLowerCase()));
        if (!wake)
            return;
        AsyncDeepSeekChat.chat(event.getUsername(), message, response ->
                event.getPlayer().getServer().getPlayerList().broadcastChatMessage(
                        PlayerChatMessage.system(response),
                        AsyncDeepSeekChat.jasonBotPlayer,
                        ChatType.bind(ChatType.CHAT, AsyncDeepSeekChat.jasonBotPlayer)
                )
        );
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        AsyncDeepSeekChat.start(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        AsyncDeepSeekChat.shutdown();
    }
}
