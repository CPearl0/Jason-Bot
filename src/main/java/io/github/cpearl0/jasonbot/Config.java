package io.github.cpearl0.jasonbot;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = JasonBot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<String> DEEPSEEK_MODEL = BUILDER
            .comment("DeepSeek Model")
            .define("DeepSeekModel", "deepseek-chat");

    private static final ForgeConfigSpec.ConfigValue<String> DEEPSEEK_API_KEY = BUILDER
            .comment("DeepSeek API Key")
            .define("DeepSeekAPIKey", "Enter your api key here");

    private static final ForgeConfigSpec.DoubleValue TEMPERATURE = BUILDER
            .comment("Model Temperature")
            .defineInRange("Temperature", 1.0, 0, 2.0);

    private static final ForgeConfigSpec.ConfigValue<String> ASSISTANT_NAME = BUILDER
            .comment("Assistant Name")
            .define("assistantName", "Jason");

    private static final ForgeConfigSpec.ConfigValue<String> SYSTEM_PROMPT = BUILDER
            .comment("System Prompt")
            .define("systemPrompt", "你叫Jason，中文名杰森，是知名Minecraft整合包作者。");

    private static final ForgeConfigSpec.IntValue MAX_HISTORY_SIZE = BUILDER
            .comment("Max History Size")
            .defineInRange("MaxHistorySize", 24, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> WAKE_NAMES = BUILDER
            .comment("Names to wake up the bot")
            .defineList("wakeNames", List.of("Jason", "杰森"), o -> true);

    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static String DeepSeekModel;
    public static String DeepSeekAPIKey;
    public static double temperature;
    public static String assistantName;
    public static String systemPrompt;
    public static int maxHistorySize;
    public static List<? extends String> wakeNames;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        DeepSeekModel = DEEPSEEK_MODEL.get();
        DeepSeekAPIKey = DEEPSEEK_API_KEY.get();
        temperature = TEMPERATURE.get();
        assistantName = ASSISTANT_NAME.get();
        systemPrompt = SYSTEM_PROMPT.get();
        maxHistorySize = MAX_HISTORY_SIZE.get();
        wakeNames = WAKE_NAMES.get();
    }
}
