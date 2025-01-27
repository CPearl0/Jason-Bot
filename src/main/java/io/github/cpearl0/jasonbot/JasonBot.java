package io.github.cpearl0.jasonbot;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JasonBot.MODID)
public class JasonBot {
    public static final String MODID = "jasonbot";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JasonBot(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
    }
}
