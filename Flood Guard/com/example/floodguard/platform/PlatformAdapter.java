package com.example.floodguard.platform;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface PlatformAdapter {
   String getPlatformName();

   String getBlockId(Block var1);

   boolean isFluidBlock(Block var1);

   void sendActionBar(Player var1, String var2);

   void logDebug(String var1);
}
