package com.example.floodguard.core;

import java.util.concurrent.atomic.AtomicLong;

public class GuardStats {
   private final AtomicLong blockedMachines = new AtomicLong();
   private final AtomicLong blockedSpreads = new AtomicLong();
   private final AtomicLong blockedExplosions = new AtomicLong();
   private final AtomicLong purgedFluids = new AtomicLong();
   private final long startedAt = System.currentTimeMillis();

   public void incrementBlockedMachines() {
      this.blockedMachines.incrementAndGet();
   }

   public void incrementBlockedSpreads() {
      this.blockedSpreads.incrementAndGet();
   }

   public void incrementBlockedExplosions() {
      this.blockedExplosions.incrementAndGet();
   }

   public void addPurgedFluids(long amount) {
      if (amount > 0L) {
         this.purgedFluids.addAndGet(amount);
      }

   }

   public long getBlockedMachines() {
      return this.blockedMachines.get();
   }

   public long getBlockedSpreads() {
      return this.blockedSpreads.get();
   }

   public long getBlockedExplosions() {
      return this.blockedExplosions.get();
   }

   public long getPurgedFluids() {
      return this.purgedFluids.get();
   }

   public long getUptimeSeconds() {
      return Math.max(0L, (System.currentTimeMillis() - this.startedAt) / 1000L);
   }

   public void reset() {
      this.blockedMachines.set(0L);
      this.blockedSpreads.set(0L);
      this.blockedExplosions.set(0L);
      this.purgedFluids.set(0L);
   }
}
