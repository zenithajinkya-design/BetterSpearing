package com.devzenith.betterspearing;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public class BetterSpearing extends JavaPlugin implements Listener {

    private final HashMap<UUID, Integer> jabCount = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BetterSpearing 1.21.11+ (Team: Dev Zenithians) is now active!");
    }

    @EventHandler
    public void onSpearJab(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Detects the 1.21.11 official Spear items
        if (item != null && item.getType().name().contains("SPEAR") && 
           (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            
            UUID uuid = player.getUniqueId();
            int counts = jabCount.getOrDefault(uuid, 0) + 1;

            if (counts >= 5) {
                // RESET AND START 5-SECOND COOLDOWN
                jabCount.put(uuid, 0);
                player.setCooldown(item.getType(), 100); 
                
                // Visual & Audio Feedback
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1.2, 0), 20, 0.3, 0.3, 0.3, 0.02);
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.8f);
                player.sendMessage("§6§lBetterSpearing §8» §eSpear is cooling down...");
            } else {
                // CONTINUE COMBO
                jabCount.put(uuid, counts);
                
                // Success sound (pitch increases with each hit)
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.8f, 1.0f + (counts * 0.2f));
                
                // Bypass vanilla cooldown for the first 4 hits
                getServer().getScheduler().runTaskLater(this, () -> {
                    player.setCooldown(item.getType(), 0);
                }, 1L);
            }
        }
    }
}
