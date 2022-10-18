package me.gavitsra.teleporters.listeners;

import me.gavitsra.teleporters.Teleporters;
import me.gavitsra.teleporters.tasks.EchoReturn;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SneakListener implements Listener {
    private final NamespacedKey isTeleporter = new NamespacedKey(Teleporters.getInstance(), "is_teleporter");
    private final NamespacedKey teleportTo = new NamespacedKey(Teleporters.getInstance(), "teleport_to");

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Location loc = event.getPlayer().getLocation();

            if (Teleporters.getInstance().teleporting.contains(event.getPlayer())) return;

            for (Entity entity : event.getPlayer().getWorld().getEntities()) {
                if (!entity.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE))
                    continue;
                if (entity.getPersistentDataContainer().get(isTeleporter, PersistentDataType.BYTE) != (byte) 1)
                    continue;

                if (!entity.getPersistentDataContainer().has(teleportTo, PersistentDataType.STRING))
                    continue;

                if (entity.getLocation().distance(loc) < 1.5) {
                    String id = entity.getPersistentDataContainer().get(teleportTo, PersistentDataType.STRING);
                    Teleporters.getInstance().teleportPlayerToPlatform(id, event.getPlayer());
                }
            }

            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if(item.hasItemMeta()) {
                if (item.getItemMeta().hasLore()) {
                    if(item.getItemMeta().getLore().get(0).equals(ChatColor.GOLD + "Portable Teleporter")) {
                        String id = item.getItemMeta().getDisplayName();
                        Teleporters.getInstance().teleportPlayerToPlatform(id, event.getPlayer());
                        int uses = Integer.parseInt(item.getItemMeta().getLore().get(1).split(" ")[0].split("")[2]);

                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GOLD + "Portable Teleporter");
                        lore.add(ChatColor.BLUE + Integer.toString(uses-1) + ChatColor.GREEN + " uses left");

                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        if (uses-1 < 1) {
                            event.getPlayer().getInventory().remove(item);
                            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        }
                    } else if(item.getItemMeta().getLore().get(0).equals(ChatColor.DARK_AQUA + "Echoing Teleporter")) {
                        if (event.getPlayer().hasCooldown(Material.ECHO_SHARD)) return;
                        event.getPlayer().setCooldown(Material.ECHO_SHARD, 20 * 60 * 5);
                        new EchoReturn(event.getPlayer(), event.getPlayer().getLocation()).run();
                        Teleporters.getInstance().teleportPlayerToPlatform(item.getItemMeta().getDisplayName(), event.getPlayer());
                    }
                }
            }
        }
    }
}
