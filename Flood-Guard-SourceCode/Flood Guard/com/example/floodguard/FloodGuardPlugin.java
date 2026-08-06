package com.example.floodguard;

import com.example.floodguard.command.FloodGuardCommand;
import com.example.floodguard.config.FloodGuardConfig;
import com.example.floodguard.core.FluidOriginTracker;
import com.example.floodguard.core.GuardService;
import com.example.floodguard.core.GuardStats;
import com.example.floodguard.detect.FluidScanner;
import com.example.floodguard.detect.FlyingMachineDetector;
import com.example.floodguard.listener.FluidSpreadListener;
import com.example.floodguard.listener.FlyingMachineListener;
import com.example.floodguard.platform.PaperPlatformAdapter;
import com.example.floodguard.platform.PlatformAdapter;
import com.example.floodguard.registry.FluidRegistry;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class FloodGuardPlugin extends JavaPlugin {
   private static FloodGuardPlugin instance;
   private FloodGuardConfig floodGuardConfig;
   private FluidRegistry fluidRegistry;
   private FluidOriginTracker originTracker;
   private FluidScanner fluidScanner;
   private FlyingMachineDetector machineDetector;
   private PlatformAdapter platformAdapter;
   private GuardService guardService;

   public static FloodGuardPlugin getInstance() {
      return instance;
   }

   public void onEnable() {
      try {
         instance = this;
         this.saveDefaultConfig();
         this.floodGuardConfig = new FloodGuardConfig(this);
         this.platformAdapter = new PaperPlatformAdapter(this, this.floodGuardConfig);
         this.fluidRegistry = new FluidRegistry(this.floodGuardConfig.getCustomFluidIds());
         this.originTracker = new FluidOriginTracker();
         this.fluidScanner = new FluidScanner(this.fluidRegistry);
         this.machineDetector = new FlyingMachineDetector(this.fluidRegistry);
         this.guardService = new GuardService(this, this.floodGuardConfig, this.fluidRegistry, this.machineDetector, this.originTracker, this.platformAdapter);
         this.getServer().getScheduler().runTaskTimer(this, () -> this.originTracker.purgeExpired(), 1200L, 1200L);
         Logger var10000 = this.getLogger();
         String var10001 = this.platformAdapter.getPlatformName();
         var10000.info("FloodGuard active sur " + var10001 + " (" + this.fluidRegistry.size() + " fluides connus).");
         this.getServer().getPluginManager().registerEvents(new FluidSpreadListener(this), this);
         this.getServer().getPluginManager().registerEvents(new FlyingMachineListener(this), this);
         FloodGuardCommand cmd = new FloodGuardCommand(this);
         if (this.getCommand("floodguard") != null) {
            if (this.getCommand("floodguard") != null && this.getCommand("floodguard") != null) {
               this.getCommand("floodguard").setExecutor(cmd);
            }

            if (this.getCommand("floodguard") != null && this.getCommand("floodguard") != null) {
               this.getCommand("floodguard").setTabCompleter(cmd);
            }
         }
      } catch (Exception e) {
         this.getLogger().severe("Failed to enable plugin: " + e.getMessage());
         e.printStackTrace();
         this.getServer().getPluginManager().disablePlugin(this);
      }

      if (this.getCommand("floodguard") != null && this.getCommand("floodguard") != null && this.getCommand("floodguard") != null) {
         this.getCommand("floodguard").setExecutor(new FloodGuardCommand(this));
      }

      if (this.getCommand("floodguard") != null && this.getCommand("floodguard") != null && this.getCommand("floodguard") != null) {
         this.getCommand("floodguard").setTabCompleter(new FloodGuardCommand(this));
      }

   }

   public void onDisable() {
      this.getServer().getScheduler().cancelTasks(this);
      if (this.originTracker != null) {
         this.originTracker.clear();
      }

      this.getLogger().info("FloodGuard desactive.");
   }

   public FloodGuardConfig getFloodGuardConfig() {
      return this.floodGuardConfig;
   }

   public GuardService getGuardService() {
      return this.guardService;
   }

   public FluidRegistry getFluidRegistry() {
      return this.fluidRegistry;
   }

   public FluidOriginTracker getOriginTracker() {
      return this.originTracker;
   }

   public FluidScanner getFluidScanner() {
      return this.fluidScanner;
   }

   public FlyingMachineDetector getMachineDetector() {
      return this.machineDetector;
   }

   public GuardStats getStats() {
      return this.guardService.getStats();
   }

   public PlatformAdapter getPlatformAdapter() {
      return this.platformAdapter;
   }

   public void reloadAll() {
      this.guardService.reload();
   }
}
