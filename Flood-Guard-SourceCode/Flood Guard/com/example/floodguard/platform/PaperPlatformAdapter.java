package com.example.floodguard.platform;

import com.example.floodguard.config.FloodGuardConfig;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatformAdapter implements PlatformAdapter {
   private final JavaPlugin plugin;
   private final FloodGuardConfig config;

   public PaperPlatformAdapter(JavaPlugin plugin, FloodGuardConfig config) {
      this.plugin = plugin;
      this.config = config;
   }

   public String getPlatformName() {
      return "Paper";
   }

   public String getBlockId(Block block) {
      if (block == null) {
         return "minecraft:air";
      } else {
         String var10000 = block.getType().name();
         return "minecraft:" + var10000.toLowerCase(Locale.ROOT);
      }
   }

   public boolean isFluidBlock(Block block) {
      Material type = block.getType();
      if (type != Material.WATER && type != Material.LAVA) {
         BlockData data = block.getBlockData();
         if (data instanceof Waterlogged) {
            Waterlogged waterlogged = (Waterlogged)data;
            if (waterlogged.isWaterlogged()) {
               return true;
            }
         }

         return data instanceof Levelled && (type == Material.WATER_CAULDRON || type == Material.LAVA_CAULDRON);
      } else {
         return true;
      }
   }

   public void sendActionBar(Player player, String message) {
      if (player != null && message != null) {
         player.sendActionBar(Component.text(message).color(NamedTextColor.RED));
      }
   }

   public void logDebug(String message) {
      if (this.config.isDebug()) {
         this.plugin.getLogger().info("[debug] " + message);
      }

   }
}
