package io.github.cpearl0.jasonbot;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = JasonBot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<String> API_ENDPOINT = BUILDER
            .comment("API Endpoint. Use DeepSeek by default.")
            .define("APIEndpoint", "https://api.deepseek.com/v1/chat/completions");

    private static final ForgeConfigSpec.ConfigValue<String> AI_MODEL = BUILDER
            .comment("AI Model. Use DeepSeek v3 by default.")
            .define("AIModel", "deepseek-chat");

    private static final ForgeConfigSpec.ConfigValue<String> API_KEY = BUILDER
            .comment("API Key")
            .define("APIKey", "Enter your api key here");

    private static final ForgeConfigSpec.DoubleValue TEMPERATURE = BUILDER
            .comment("Model Temperature")
            .defineInRange("temperature", 1.0, 0, 2.0);

    private static final ForgeConfigSpec.DoubleValue PRESENCE_PENALTY = BUILDER
            .comment("Model Presence Penalty")
            .defineInRange("presencePenalty", 1.0, -2.0, 2.0);

    private static final ForgeConfigSpec.ConfigValue<String> ASSISTANT_NAME = BUILDER
            .comment("Assistant Name")
            .define("assistantName", "Jason");

    private static final ForgeConfigSpec.ConfigValue<String> SYSTEM_PROMPT = BUILDER
            .comment("System Prompt")
            .define("systemPrompt",
                    "你叫Jason，中文名杰森，是知名Minecraft整合包作者。这里是一个Minecraft服务器，玩家会向你提问、和你聊天，请和他们友善而活泼地互动。");

    private static final ForgeConfigSpec.IntValue MAX_HISTORY_SIZE = BUILDER
            .comment("Max History Size. Set to 0 if you don't want the bot to use history.")
            .defineInRange("maxHistorySize", 24, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> WAKE_NAMES = BUILDER
            .comment("Names to wake up the bot")
            .defineList("wakeNames", List.of("Jason", "杰森"), o -> true);

    private static final ForgeConfigSpec.BooleanValue USE_IN_GAME_INFORMATION = BUILDER
            .comment("Whether to provide in-game information to the bot")
            .define("useInGameInformation", true);

    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static String APIEndpoint;
    public static String AIModel;
    public static String APIKey;
    public static double temperature;
    public static double presencePenalty;
    public static String assistantName;
    public static String systemPrompt;
    public static int maxHistorySize;
    public static List<? extends String> wakeNames;
    public static boolean useInGameInformation;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        APIEndpoint = API_ENDPOINT.get();
        AIModel = AI_MODEL.get();
        APIKey = API_KEY.get();
        temperature = TEMPERATURE.get();
        presencePenalty = PRESENCE_PENALTY.get();
        assistantName = ASSISTANT_NAME.get();
        systemPrompt = SYSTEM_PROMPT.get();
        maxHistorySize = MAX_HISTORY_SIZE.get();
        wakeNames = WAKE_NAMES.get();
        useInGameInformation = USE_IN_GAME_INFORMATION.get();
    }
}
