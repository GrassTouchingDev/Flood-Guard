package com.example.floodguard.command;

import com.example.floodguard.FloodGuardPlugin;
import com.example.floodguard.config.FloodGuardConfig;
import com.example.floodguard.core.GuardService;
import com.example.floodguard.core.GuardStats;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class FloodGuardCommand implements CommandExecutor, TabCompleter {
   private static final List<String> SUB_COMMANDS = List.of("reload", "status", "stats", "toggle", "radius", "purge");
   private final FloodGuardPlugin plugin;

   public FloodGuardCommand(FloodGuardPlugin plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("floodguard.admin") && !sender.hasPermission("floodguard.command.status")) {
         sender.sendMessage(Component.text("Tu n'as pas la permission d'utiliser FloodGuard.").color(NamedTextColor.RED));
         return true;
      } else if (args.length == 0) {
         this.sendUsage(sender, label);
         return true;
      } else {
         String sub = args[0].toLowerCase(Locale.ROOT);
         GuardService service = this.plugin.getGuardService();
         FloodGuardConfig config = this.plugin.getFloodGuardConfig();
         switch (sub) {
            case "reload":
               if (!this.has(sender, "floodguard.command.reload")) {
                  return true;
               }

               service.reload();
               sender.sendMessage(Component.text("Configuration FloodGuard rechargee (" + this.plugin.getFluidRegistry().size() + " fluides connus).").color(NamedTextColor.GREEN));
               return true;
            case "status":
               if (!this.has(sender, "floodguard.command.status")) {
                  return true;
               }

               sender.sendMessage(Component.text("=== FloodGuard - Etat ===").color(NamedTextColor.GOLD));
               String var10001 = config.isEnabled() ? "oui" : "non";
               sender.sendMessage(Component.text("Actif : " + var10001).color(config.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED));
               sender.sendMessage(Component.text("Plateforme : " + this.plugin.getPlatformAdapter().getPlatformName()).color(NamedTextColor.AQUA));
               int var20 = config.getFluidDetectionRadius();
               sender.sendMessage(Component.text("Rayon fluides : " + var20 + " | Rayon machines : " + config.getMachineDetectionRadius() + " | Propagation max : " + config.getMaxFluidSpread()).color(NamedTextColor.YELLOW));
               var20 = config.isBlockPistons();
               sender.sendMessage(Component.text("Pistons bloques : " + var20 + " | TNT bloquee : " + config.isBlockTnt()).color(NamedTextColor.YELLOW));
               String var22 = config.getDisabledWorlds().isEmpty() ? "aucun" : String.join(", ", config.getDisabledWorlds());
               sender.sendMessage(Component.text("Mondes desactives : " + var22).color(NamedTextColor.GRAY));
               return true;
            case "stats":
               if (!this.has(sender, "floodguard.command.stats")) {
                  return true;
               } else {
                  GuardStats stats = this.plugin.getStats();
                  if (args.length >= 2 && args[1].equalsIgnoreCase("reset")) {
                     stats.reset();
                     sender.sendMessage(Component.text("Statistiques FloodGuard remises a zero.").color(NamedTextColor.GREEN));
                     return true;
                  }

                  sender.sendMessage(Component.text("=== FloodGuard - Statistiques ===").color(NamedTextColor.GOLD));
                  sender.sendMessage(Component.text("Machines bloquees : " + stats.getBlockedMachines()).color(NamedTextColor.YELLOW));
                  sender.sendMessage(Component.text("Propagations bloquees : " + stats.getBlockedSpreads()).color(NamedTextColor.YELLOW));
                  sender.sendMessage(Component.text("Explosions bloquees : " + stats.getBlockedExplosions()).color(NamedTextColor.YELLOW));
                  sender.sendMessage(Component.text("Fluides purges : " + stats.getPurgedFluids()).color(NamedTextColor.YELLOW));
                  sender.sendMessage(Component.text("Uptime : " + stats.getUptimeSeconds() + "s").color(NamedTextColor.GRAY));
                  return true;
               }
            case "toggle":
               if (!this.has(sender, "floodguard.command.toggle")) {
                  return true;
               }

               boolean newValue = !config.isEnabled();
               this.plugin.getConfig().set("enabled", newValue);
               this.plugin.saveConfig();
               service.reload();
               sender.sendMessage(Component.text("FloodGuard est maintenant " + (newValue ? "ACTIVE" : "DESACTIVE") + ".").color(newValue ? NamedTextColor.GREEN : NamedTextColor.RED));
               return true;
            case "radius":
               if (!this.has(sender, "floodguard.admin")) {
                  return true;
               } else {
                  int value = this.parseInt(args[2], -1);
                  if (value >= 0 && value <= 32) {
                     switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "fluid":
                           this.plugin.getConfig().set("detection.fluid-radius", Math.max(1, value));
                           break;
                        case "machine":
                           this.plugin.getConfig().set("detection.machine-radius", Math.max(1, value));
                           break;
                        case "spread":
                           this.plugin.getConfig().set("spread.max-blocks", value);
                           break;
                        default:
                           sender.sendMessage(Component.text("Type inconnu : fluid, machine ou spread.").color(NamedTextColor.RED));
                           return true;
                     }

                     this.plugin.saveConfig();
                     service.reload();
                     sender.sendMessage(Component.text("Rayon '" + type + "' regle sur " + value + ".").color(NamedTextColor.GREEN));
                     return true;
                  }

                  sender.sendMessage(Component.text("Valeur invalide (0-32).").color(NamedTextColor.RED));
                  return true;
               }
            case "purge":
               if (!this.has(sender, "floodguard.admin")) {
                  return true;
               } else {
                  if (sender instanceof Player) {
                     Player player = (Player)sender;
                     int radius = args.length >= 2 ? this.parseInt(args[1], 5) : 5;
                     radius = Math.max(1, Math.min(16, radius));
                     Location center = player.getLocation();
                     int removed = service.purgeFluids(center, radius);
                     player.sendMessage(Component.text(removed + " bloc(s) de fluide supprime(s) dans un rayon de " + radius + ".").color(NamedTextColor.GREEN));
                     return true;
                  }

                  sender.sendMessage(Component.text("Commande reservee aux joueurs.").color(NamedTextColor.RED));
                  return true;
               }
            default:
               this.sendUsage(sender, label);
               return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 3 && args[0].equalsIgnoreCase("radius")) {
         for(String r : List.of("1", "2", "3", "5")) {
            if (r.startsWith(args[2])) {
               out.add(r);
            }
         }
      }

      return out;
   }

   private boolean has(CommandSender sender, String permission) {
      if (!sender.hasPermission(permission) && !sender.hasPermission("floodguard.admin")) {
         sender.sendMessage(Component.text("Permission manquante : " + permission).color(NamedTextColor.RED));
         return false;
      } else {
         return true;
      }
   }

   private void sendUsage(CommandSender sender, String label) {
      sender.sendMessage(Component.text("=== FloodGuard ===").color(NamedTextColor.GOLD));
      sender.sendMessage(Component.text("/" + label + " reload - recharge la configuration").color(NamedTextColor.YELLOW));
      sender.sendMessage(Component.text("/" + label + " status - etat de la protection").color(NamedTextColor.YELLOW));
      sender.sendMessage(Component.text("/" + label + " stats [reset] - statistiques").color(NamedTextColor.YELLOW));
      sender.sendMessage(Component.text("/" + label + " toggle - active/desactive FloodGuard").color(NamedTextColor.YELLOW));
      sender.sendMessage(Component.text("/" + label + " radius <fluid|machine|spread> <valeur>").color(NamedTextColor.YELLOW));
      sender.sendMessage(Component.text("/" + label + " purge [rayon] - supprime les fluides autour de toi").color(NamedTextColor.YELLOW));
   }

   private int parseInt(String raw, int fallback) {
      try {
         return Integer.parseInt(raw);
      } catch (NumberFormatException var4) {
         return fallback;
      }
   }
}
