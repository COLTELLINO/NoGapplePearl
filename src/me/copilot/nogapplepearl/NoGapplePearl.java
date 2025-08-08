package me.copilot.nogapplepearl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.Player;

import java.util.List;

public class NoGapplePearl extends JavaPlugin implements Listener {
    private List<String> blockedWorlds;
    private boolean blockEnderPearls;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();
        blockedWorlds = config.getStringList("blocked-worlds");
        blockEnderPearls = config.getBoolean("block-enderpearls", true);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        Material mat = item.getType();
        // Blocca golden apple e enchanted golden apple
        if (mat == Material.GOLDEN_APPLE) {
            if (item.getDurability() == 0 || item.getDurability() == 1) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cNon puoi usare le golden apple!");
                return;
            }
        }
        // Blocca tutte le pozioni tranne Instant Health I e II
        if (mat == Material.POTION) {
            Potion potion = Potion.fromItemStack(item);
            if (potion.getType() != PotionType.INSTANT_HEAL || (potion.getLevel() != 1 && potion.getLevel() != 2)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cQuesta pozione non è consentita!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (blockEnderPearls && blockedWorlds.contains(world.getName()) && item.getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
            player.sendMessage("§cNon puoi usare le ender pearl in questo mondo!");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            if (blockEnderPearls && blockedWorlds.contains(world.getName())) {
                event.setCancelled(true);
                player.sendMessage("§cNon puoi usare le ender pearl in questo mondo!");
            }
        }
    }
}
