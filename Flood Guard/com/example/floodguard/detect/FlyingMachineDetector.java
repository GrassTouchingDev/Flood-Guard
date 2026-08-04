package com.example.floodguard.detect;

import com.example.floodguard.registry.FluidRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class FlyingMachineDetector {
   private static final Set<Material> MACHINE_PARTS;
   private final FluidRegistry registry;

   public FlyingMachineDetector(FluidRegistry registry) {
      this.registry = registry;
   }

   public boolean isMachinePart(Material material) {
      return material != null && MACHINE_PARTS.contains(material);
   }

   public boolean isMachineNearby(Location center, int radius) {
      return this.countMachineParts(center, radius) > 0;
   }

   public int countMachineParts(Location center, int radius) {
      int count = 0;

      for(Block block : this.scan(center, radius)) {
         if (this.isMachinePart(block.getType())) {
            ++count;
         }
      }

      return count;
   }

   public List<Block> findFluids(Location center, int radius) {
      List<Block> fluids = new ArrayList();

      for(Block block : this.scan(center, radius)) {
         if (this.registry.isFluid(block)) {
            fluids.add(block);
         }
      }

      return fluids;
   }

   public boolean hasFluidNearby(Location center, int radius) {
      return !this.findFluids(center, radius).isEmpty();
   }

   public List<Block> scan(Location center, int radius) {
      List<Block> blocks = new ArrayList();
      if (center != null && center.getWorld() != null && radius >= 0) {
         World world = center.getWorld();
         int cx = center.getBlockX();

         for(int x = cx - radius; x <= cx + radius; ++x) {
         }

         return blocks;
      } else {
         return blocks;
      }
   }

   static {
      MACHINE_PARTS = EnumSet.of(Material.PISTON, Material.STICKY_PISTON, Material.MOVING_PISTON, Material.PISTON_HEAD, Material.SLIME_BLOCK, Material.HONEY_BLOCK, Material.OBSERVER, Material.REDSTONE_BLOCK);
   }
}
