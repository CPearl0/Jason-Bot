package io.github.cpearl0.jasonbot.bot;

import io.github.cpearl0.jasonbot.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.time.LocalDateTime;

public class PromptGenerator {
    public static String generatePrompt(ServerPlayer player) {
        StringBuilder prompt = new StringBuilder(Config.systemPrompt);

        if (Config.useInGameInformation) {
            prompt.append("\n下面是正在与你对话的玩家的一些信息。" +
                    "你不需要主动向玩家提供这些信息，而应该在回答有关问题时参照。" +
                    "注意：玩家信息可能发生变化，因为玩家在两次对话间会采取动作，以接下来最先看到的信息为准。");

            var level = player.level();

            var dimension = level.dimension().toString();
            prompt.append("\n玩家所在维度：");
            prompt.append(dimension);

            var dayTime = level.getDayTime();
            prompt.append("\n游戏内时间：");
            prompt.append(dayTime);

            var datetime = LocalDateTime.now();
            prompt.append("\n现实时间（注意这与游戏无关，仅在玩家与你讨论现实世界时使用）：");
            prompt.append(datetime);

            var pos = player.getOnPos();
            prompt.append("\n玩家坐标：");
            prompt.append("x:%d y:%d z:%d".formatted(pos.getX(), pos.getY(), pos.getZ()));

            var biome = level.getBiome(pos).unwrap().map(key -> key.location().toString(), ubiome -> "[unknown biome]");
            prompt.append("\n玩家所在生物群系：");
            prompt.append(biome);

            var health = player.getHealth();
            prompt.append("\n玩家生命值：");
            prompt.append(health);

            var hunger = player.getFoodData().getFoodLevel();
            prompt.append("\n玩家饥饿值：");
            prompt.append(hunger);

            var saturation = player.getFoodData().getSaturationLevel();
            prompt.append("\n玩家饱腹度：");
            prompt.append(saturation);

            var experienceLevel = player.experienceLevel;
            prompt.append("\n玩家的经验等级：");
            prompt.append(experienceLevel);

            var mainhandItem = player.getMainHandItem();
            prompt.append("\n玩家主手物品：");
            prompt.append(mainhandItem);
            prompt.append("\n玩家主手物品NBT：");
            prompt.append(mainhandItem.getTag());

            var offhandItem = player.getOffhandItem();
            prompt.append("\n玩家副手物品：");
            prompt.append(offhandItem);
            prompt.append("\n玩家副手物品NBT：");
            prompt.append(offhandItem.getTag());

            var armor = player.getArmorSlots();
            prompt.append("\n玩家装备：");
            prompt.append(armor);

//            var inventory = player.getInventory();
//            prompt.append("\n玩家背包：");
//            prompt.append(inventory.items);

            var block = player.pick(20.0, 0.0F, false);
            var liquid = player.pick(20.0, 0.0F, true);
            if (block.getType() == HitResult.Type.BLOCK) {
                var blockpos = ((BlockHitResult) block).getBlockPos();
                var blockstate = level.getBlockState(blockpos);
                var blockname = BuiltInRegistries.BLOCK.getKey(blockstate.getBlock());
                prompt.append("\n玩家视线内方块：");
                prompt.append(blockname);
            }
            if (liquid.getType() == HitResult.Type.BLOCK) {
                var blockpos = ((BlockHitResult) liquid).getBlockPos();
                var fluidstate = level.getFluidState(blockpos);
                var fluidname = BuiltInRegistries.FLUID.getKey(fluidstate.getType());
                prompt.append("\n玩家视线内流体：");
                prompt.append(fluidname);
            }

            var playerCount = level.players().size();
            prompt.append("\n服务器在线人数：");
            prompt.append(playerCount);

            var nearestPlayer = level.getNearestPlayer(player.getX(), player.getY(), player.getZ(), -1.0, p -> p != player && EntitySelector.NO_SPECTATORS.test(p));
            if (nearestPlayer != null) {
                prompt.append("\n玩家附近最近的玩家是：");
                prompt.append(nearestPlayer.getDisplayName().getString());
                prompt.append("\n其位置是：");
                prompt.append(nearestPlayer.getOnPos());
            }
        }

        return prompt.toString();
    }
}
