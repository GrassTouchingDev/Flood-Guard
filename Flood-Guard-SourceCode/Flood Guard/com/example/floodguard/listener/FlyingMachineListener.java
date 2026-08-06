package com.example.floodguard.listener;

import com.example.floodguard.FloodGuardPlugin;
import com.example.floodguard.config.FloodGuardConfig;
import com.example.floodguard.core.GuardService;
import com.example.floodguard.core.GuardStats;
import com.example.floodguard.detect.FluidScanner;
import com.example.floodguard.detect.FlyingMachineDetector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class FlyingMachineListener implements Listener {
   private final FloodGuardPlugin plugin;
   private final GuardService guard;
   private final FlyingMachineDetector detector;
   private final FluidScanner scanner;
   private final FloodGuardConfig config;
   private final GuardStats stats;

   public FlyingMachineListener(FloodGuardPlugin plugin) {
      this.plugin = plugin;
      this.guard = plugin.getGuardService();
      this.detector = plugin.getMachineDetector();
      this.scanner = plugin.getFluidScanner();
      this.config = plugin.getFloodGuardConfig();
      this.stats = plugin.getStats();
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPistonExtend(BlockPistonExtendEvent event) {
      this.handlePiston(event.getBlock(), event);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPistonRetract(BlockPistonRetractEvent event) {
      this.handlePiston(event.getBlock(), event);
   }

   private void handlePiston(Block piston, Cancellable event) {
      Location loc = piston.getLocation();
      if (this.guard.isActiveAt(loc)) {
         if (this.guard.shouldBlockMachine(piston)) {
            event.setCancelled(true);
            this.stats.incrementBlockedMachines();
            this.notifyNearby(loc);
         }
      }
   }

   private void notifyNearby(Location loc) {
      int radius = Math.max(this.config.getMachineDetectionRadius(), this.config.getFluidDetectionRadius()) + 8;
      int fluids = this.scanner.countFluids(loc, this.config.getFluidDetectionRadius());
      int parts = this.detector.countMachineParts(loc, this.config.getMachineDetectionRadius());
      Component msg = Component.text("FloodGuard : machine volante bloquée (" + fluids + " fluide(s), " + parts + " pièce(s) de machine) en " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()).color(NamedTextColor.RED);
      double maxDistSq = (double)radius * (double)radius;

      for(Player player : loc.getWorld().getPlayers()) {
         Location playerLoc = player.getLocation();
         if (playerLoc.getWorld() == loc.getWorld() && !(playerLoc.distanceSquared(loc) > maxDistSq) && !player.hasPermission("floodguard.bypass")) {
            this.plugin.getPlatformAdapter().sendActionBar(player, "FloodGuard : machine volante bloquée, fluide détecté à proximité !");
            if (player.hasPermission("floodguard.notify")) {
               player.sendMessage(msg);
            }
         }
      }

   }
}
