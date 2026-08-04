package com.example.floodguard.core;

import com.example.floodguard.config.FloodGuardConfig;
import com.example.floodguard.detect.FlyingMachineDetector;
import com.example.floodguard.platform.PlatformAdapter;
import com.example.floodguard.registry.FluidRegistry;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GuardService {
   private final JavaPlugin plugin;
   private final FloodGuardConfig config;
   private final FluidRegistry registry;
   private final FlyingMachineDetector detector;
   private final FluidOriginTracker tracker;
   private final PlatformAdapter platform;
   private final GuardStats stats = new GuardStats();

   public GuardService(JavaPlugin plugin, FloodGuardConfig config, FluidRegistry registry, FlyingMachineDetector detector, FluidOriginTracker tracker, PlatformAdapter platform) {
      this.plugin = plugin;
      this.config = config;
      this.registry = registry;
      this.detector = detector;
      this.tracker = tracker;
      this.platform = platform;
   }

   public boolean isActiveAt(Location location) {
      return location != null && location.getWorld() != null && this.config.isEnabledInWorld(location.getWorld().getName());
   }

   public boolean shouldBlockMachine(Block pistonBlock) {
      if (pistonBlock != null && this.config.isBlockPistons()) {
         Location loc = pistonBlock.getLocation();
         if (!this.isActiveAt(loc)) {
            return false;
         } else {
            boolean fluid = this.detector.hasFluidNearby(loc, this.config.getFluidDetectionRadius());
            if (fluid) {
               PlatformAdapter var10000 = this.platform;
               String var10001 = this.platform.getBlockId(pistonBlock);
               var10000.logDebug("Machine bloquee en " + var10001 + " @ " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            }

            return fluid;
         }
      } else {
         return false;
      }
   }

   public boolean shouldBlockSpread(Block from, Block to) {
      if (from != null && to != null) {
         Location loc = to.getLocation();
         if (!this.isActiveAt(loc)) {
            return false;
         } else if (!this.registry.isFluid(from) && !this.registry.isFluid(to.getType())) {
            return false;
         } else {
            boolean machineNearby = this.detector.isMachineNearby(loc, this.config.getMachineDetectionRadius());
            if (!machineNearby) {
               this.tracker.forget(to);
               return false;
            } else {
               int distance = this.tracker.propagate(from, to);
               if (distance > this.config.getMaxFluidSpread()) {
                  this.tracker.forget(to);
                  this.platform.logDebug("Propagation bloquee a " + distance + " blocs de l'origine.");
                  return true;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldBlockExplosion(Location location) {
      if (location != null && this.config.isBlockTnt() && this.isActiveAt(location)) {
         return this.detector.hasFluidNearby(location, this.config.getFluidDetectionRadius()) && this.detector.isMachineNearby(location, this.config.getMachineDetectionRadius());
      } else {
         return false;
      }
   }

   public int purgeFluids(Location center, int radius) {
      if (center != null && center.getWorld() != null) {
         List<Block> fluids = this.detector.findFluids(center, radius);
         int removed = 0;

         for(Block block : fluids) {
            if (block.getType() != Material.AIR) {
               block.setType(Material.AIR, false);
               this.tracker.forget(block);
               ++removed;
            }
         }

         this.stats.addPurgedFluids((long)removed);
         this.platform.logDebug("Purge de " + removed + " fluides.");
         return removed;
      } else {
         return 0;
      }
   }

   public void notifyMachineBlocked(Player player) {
      this.stats.incrementBlockedMachines();
      if (player != null) {
         this.platform.sendActionBar(player, this.config.getMessageBlockedMachine());
      }

   }

   public void notifySpreadBlocked() {
      this.stats.incrementBlockedSpreads();
   }

   public void notifyExplosionBlocked() {
      this.stats.incrementBlockedExplosions();
   }

   public void reload() {
      this.config.load();
      this.registry.reload(this.config.getCustomFluidIds());
      this.tracker.clear();
      this.plugin.getLogger().info("FloodGuard recharge (" + this.registry.size() + " fluides connus).");
   }

   public GuardStats getStats() {
      return this.stats;
   }

   public FloodGuardConfig getConfig() {
      return this.config;
   }

   public FluidRegistry getRegistry() {
      return this.registry;
   }

   public FluidOriginTracker getTracker() {
      return this.tracker;
   }

   public FlyingMachineDetector getDetector() {
      return this.detector;
   }

   public PlatformAdapter getPlatform() {
      return this.platform;
   }
}
