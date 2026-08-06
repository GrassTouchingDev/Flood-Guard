package com.example.floodguard.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.block.Block;

public class FluidOriginTracker {
   private final Map<String, Origin> origins = new ConcurrentHashMap();
   private long entryLifetimeMillis = 300000L;

   public static String key(Block block) {
      String var10000 = block.getWorld().getName();
      return var10000 + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
   }

   public void markSource(Block source) {
      if (source != null) {
         this.origins.put(key(source), new Origin(key(source), 0, System.currentTimeMillis()));
      }
   }

   public int propagate(Block from, Block to) {
      if (from != null && to != null) {
         Origin parent = (Origin)this.origins.get(key(from));
         if (parent == null) {
            parent = new Origin(key(from), 0, System.currentTimeMillis());
            this.origins.put(key(from), parent);
         }

         int distance = parent.distance + 1;
         this.origins.put(key(to), new Origin(parent.originKey, distance, System.currentTimeMillis()));
         return distance;
      } else {
         return 0;
      }
   }

   public int getDistance(Block block) {
      if (block == null) {
         return 0;
      } else {
         Origin origin = (Origin)this.origins.get(key(block));
         return origin == null ? 0 : origin.distance;
      }
   }

   public boolean isTracked(Block block) {
      return block != null && this.origins.containsKey(key(block));
   }

   public void forget(Block block) {
      this.origins.remove(key(block));
   }

   public int size() {
      return this.origins.size();
   }

   public void clear() {
      this.origins.clear();
   }

   public void setEntryLifetimeMillis(long millis) {
      if (millis > 0L) {
         this.entryLifetimeMillis = millis;
      }

   }

   public int purgeExpired() {
      long now = System.currentTimeMillis();
      int before = this.origins.size();
      this.origins.values().removeIf((origin) -> now - origin.createdAt > this.entryLifetimeMillis);
      return before - this.origins.size();
   }

   private static final class Origin {
      private String originKey;
      private int distance;
      private long createdAt;

      private Origin(String originKey, int distance, long createdAt) {
         this.originKey = originKey;
         this.distance = distance;
         this.createdAt = createdAt;
      }
   }
}
