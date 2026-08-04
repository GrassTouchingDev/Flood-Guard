package com.example.floodguard.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FloodGuardConfig {
   private final JavaPlugin plugin;
   private boolean enabled = true;
   private boolean debug = false;
   private int fluidDetectionRadius = 3;
   private int machineDetectionRadius = 3;
   private int maxFluidSpread = 2;
   private boolean blockPistons = true;
   private boolean blockTnt = true;
   private List<String> disabledWorlds = new ArrayList();
   private List<String> customFluidIds = new ArrayList();
   private String messageBlockedMachine = "[FloodGuard] Machine volante bloquee : fluide detecte a proximite.";
   private String messageBlockedSpread = "[FloodGuard] Propagation de fluide limitee.";

   public FloodGuardConfig(JavaPlugin plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.plugin.saveDefaultConfig();
      this.plugin.reloadConfig();
      FileConfiguration cfg = this.plugin.getConfig();
      this.enabled = cfg.getBoolean("enabled", true);
      this.debug = cfg.getBoolean("debug", false);
      this.fluidDetectionRadius = Math.max(1, cfg.getInt("detection.fluid-radius", 3));
      this.machineDetectionRadius = Math.max(1, cfg.getInt("detection.machine-radius", 3));
      this.maxFluidSpread = Math.max(0, cfg.getInt("spread.max-blocks", 2));
      this.blockPistons = cfg.getBoolean("block.pistons", true);
      this.blockTnt = cfg.getBoolean("block.tnt", true);
      this.disabledWorlds = new ArrayList(cfg.getStringList("worlds.disabled"));
      List<String> custom = new ArrayList();

      for(String id : cfg.getStringList("fluids.custom")) {
         if (id != null && !id.isBlank()) {
            custom.add(id.toLowerCase(Locale.ROOT).trim());
         }
      }

      this.customFluidIds = custom;
      this.messageBlockedMachine = cfg.getString("messages.blocked-machine", this.messageBlockedMachine);
      this.messageBlockedSpread = cfg.getString("messages.blocked-spread", this.messageBlockedSpread);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getFluidDetectionRadius() {
      return this.fluidDetectionRadius;
   }

   public int getMachineDetectionRadius() {
      return this.machineDetectionRadius;
   }

   public int getMaxFluidSpread() {
      return this.maxFluidSpread;
   }

   public boolean isEnabledInWorld(String worldName) {
      if (this.enabled && worldName != null) {
         for(String disabled : this.disabledWorlds) {
            if (disabled.equalsIgnoreCase(worldName)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isDebug() {
      return this.debug;
   }

   public List<String> getCustomFluidIds() {
      return this.customFluidIds;
   }

   public List<String> getDisabledWorlds() {
      return this.disabledWorlds;
   }

   public boolean isBlockPistons() {
      return this.blockPistons;
   }

   public boolean isBlockTnt() {
      return this.blockTnt;
   }

   public String getMessageBlockedMachine() {
      return this.messageBlockedMachine;
   }

   public String getMessageBlockedSpread() {
      return this.messageBlockedSpread;
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }
}
