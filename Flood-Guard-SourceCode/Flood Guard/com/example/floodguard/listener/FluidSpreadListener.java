package com.example.floodguard.listener;

import com.example.floodguard.FloodGuardPlugin;
import com.example.floodguard.config.FloodGuardConfig;
import com.example.floodguard.core.FluidOriginTracker;
import com.example.floodguard.core.GuardService;
import com.example.floodguard.platform.PlatformAdapter;
import com.example.floodguard.registry.FluidRegistry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class FluidSpreadListener implements Listener {
   private final FloodGuardPlugin plugin;
   private final GuardService guard;
   private final FluidOriginTracker tracker;
   private final FluidRegistry registry;
   private final FloodGuardConfig config;

   public FluidSpreadListener(FloodGuardPlugin plugin) {
      this.plugin = plugin;
      this.guard = plugin.getGuardService();
      this.tracker = plugin.getOriginTracker();
      this.registry = plugin.getFluidRegistry();
      this.config = plugin.getFloodGuardConfig();
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBlockFromTo(BlockFromToEvent event) {
      Block from = event.getBlock();
      Block to = event.getToBlock();
      if (this.registry.isFluid(from) || this.registry.isFluid(to.getType())) {
         if (this.guard.isActiveAt(to.getLocation())) {
            if (this.guard.shouldBlockSpread(from, to)) {
               event.setCancelled(true);
               this.guard.notifySpreadBlocked();
               PlatformAdapter var10000 = this.plugin.getPlatformAdapter();
               int var10001 = to.getX();
               var10000.logDebug("Propagation annulee vers " + var10001 + "," + to.getY() + "," + to.getZ() + " (max " + this.config.getMaxFluidSpread() + " blocs).");
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBlockPlace(BlockPlaceEvent event) {
      Block placed = event.getBlockPlaced();
      if (this.guard.isActiveAt(placed.getLocation())) {
         if (this.registry.isFluid(placed)) {
            this.tracker.markSource(placed);
            Player player = event.getPlayer();
            if (!player.hasPermission("floodguard.bypass")) {
               this.plugin.getPlatformAdapter().logDebug("Nouvelle origine de fluide posee par " + player.getName() + ".");
            }
         }

      }
   }
}
