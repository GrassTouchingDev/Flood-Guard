package com.example.floodguard.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

public class FluidRegistry {
   private static final Set<Material> VANILLA_FLUIDS;
   private final Set<NamespacedKey> registeredKeys = new LinkedHashSet();
   private final Set<Material> customFluids = new LinkedHashSet();

   public FluidRegistry(List<String> extraFluidKeys) {
      this.reload(extraFluidKeys);
   }

   public final void reload(List<String> extraFluidKeys) {
      this.registeredKeys.clear();
      this.customFluids.clear();

      for(Material vanilla : VANILLA_FLUIDS) {
         String vanillaPath = vanilla.name().toLowerCase(Locale.ROOT);
         this.registeredKeys.add(NamespacedKey.minecraft(vanillaPath));
      }

      if (extraFluidKeys != null) {
         for(String raw : extraFluidKeys) {
            if (raw != null && !raw.isBlank()) {
               String id = raw.toLowerCase(Locale.ROOT).trim();
               String namespace = "minecraft";
               String path = id;
               int sep = id.indexOf(58);
               if (sep > 0) {
                  namespace = id.substring(0, sep);
                  path = id.substring(sep + 1);
               }

               if (!path.isEmpty()) {
                  NamespacedKey key = new NamespacedKey(namespace, path);
                  this.registeredKeys.add(key);
                  if ("minecraft".equals(namespace)) {
                     Material material = Material.matchMaterial(namespace + ":" + path);
                     if (material != null && !VANILLA_FLUIDS.contains(material)) {
                        this.customFluids.add(material);
                     }
                  }
               }
            }
         }

      }
   }

   public boolean isVanillaFluid(Material material) {
      return material != null && VANILLA_FLUIDS.contains(material);
   }

   public boolean isCustomFluid(Material material) {
      return material != null && this.customFluids.contains(material);
   }

   public boolean isFluid(Material material) {
      return this.isVanillaFluid(material) || this.isCustomFluid(material);
   }

   public boolean isFluid(Block block) {
      if (block == null) {
         return false;
      } else if (this.isFluid(block.getType())) {
         return true;
      } else {
         BlockData data = block.getBlockData();
         boolean var10000;
         if (data instanceof Waterlogged) {
            Waterlogged waterlogged = (Waterlogged)data;
            if (waterlogged.isWaterlogged()) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public Set<NamespacedKey> getRegisteredKeys() {
      return Collections.unmodifiableSet(new LinkedHashSet(this.registeredKeys));
   }

   public int size() {
      return this.registeredKeys.size();
   }

   public List<String> keysAsStrings() {
      List<String> out = new ArrayList();

      for(NamespacedKey key : this.registeredKeys) {
         out.add(key.toString());
      }

      return out;
   }

   static {
      VANILLA_FLUIDS = Collections.unmodifiableSet(new LinkedHashSet(List.of(Material.WATER, Material.LAVA, Material.BUBBLE_COLUMN, Material.WATER_CAULDRON, Material.LAVA_CAULDRON, Material.POWDER_SNOW_CAULDRON)));
   }
}
