package com.example.floodguard.detect;

import com.example.floodguard.registry.FluidRegistry;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class FluidScanner {
   private final FluidRegistry registry;

   public FluidScanner(FluidRegistry registry) {
      this.registry = registry;
   }

   public boolean hasFluidNearby(Location center, int radius) {
      return this.findNearestFluid(center, radius) != null;
   }

   public List<Block> findFluids(Location center, int radius) {
      List<Block> found = new ArrayList();
      if (center != null && center.getWorld() != null) {
         World world = center.getWorld();
         int cx = center.getBlockX();

         for(int x = cx - radius; x <= cx + radius; ++x) {
         }

         return found;
      } else {
         return found;
      }
   }

   public Block findNearestFluid(Location center, int radius) {
      if (center != null && center.getWorld() != null) {
         Block nearest = null;
         double best = Double.MAX_VALUE;

         for(Block block : this.findFluids(center, radius)) {
            double dist = block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F).distanceSquared(center);
            if (dist < best) {
               best = dist;
               nearest = block;
            }
         }

         return nearest;
      } else {
         return null;
      }
   }

   public int countFluids(Location center, int radius) {
      return this.findFluids(center, radius).size();
   }
}
