package me.gavitsra.teleporters.listeners;

import me.gavitsra.teleporters.Teleporters;
import me.gavitsra.teleporters.tasks.EchoReturn;
import me.gavitsra.teleporters.tasks.Teleport;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
            Player player = event.getPlayer();
            Location loc = player.getLocation();

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

            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.hasItemMeta()) {
                if (item.getItemMeta().hasLore()) {
                    if(item.getItemMeta().getLore().get(0).equals(ChatColor.GOLD + "Portable Teleporter")) {
                        String id = item.getItemMeta().getDisplayName();
                        Teleporters.getInstance().teleportPlayerToPlatform(id, player);
                        int uses = Integer.parseInt(item.getItemMeta().getLore().get(1).split(" ")[0].split("")[2]);

                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GOLD + "Portable Teleporter");
                        lore.add(ChatColor.BLUE + Integer.toString(uses-1) + ChatColor.GREEN + " uses left");

                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        if (uses-1 < 1) {
                            player.getInventory().remove(item);
                            player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        }
                    } else if(item.getItemMeta().getLore().get(0).equals(ChatColor.DARK_AQUA + "Echoing Teleporter")) {
                        if (Teleporters.getInstance().echoing.containsKey(player)) {
                            Location toLoc = Teleporters.getInstance().echoing.get(player);
                            Teleporters.getInstance().teleportPlayerToLocation(toLoc, player);
                            player.setCooldown(Material.ECHO_SHARD, 20 * 60 * 4);
                            Teleporters.getInstance().echoing.remove(player);
                            return;
                        }

                        if (player.hasCooldown(Material.ECHO_SHARD)) return;
                        Teleporters.getInstance().echoing.put(player, player.getLocation());
                        player.setCooldown(Material.ECHO_SHARD, 20 * 60);
                        new EchoReturn(player, player.getLocation()).run();
                        Teleporters.getInstance().teleportPlayerToPlatform(item.getItemMeta().getDisplayName(), player);
                    }
                }
            }
        }
    }
}
