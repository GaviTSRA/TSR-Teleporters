package me.gavitsra.teleporters.listeners;

import me.gavitsra.teleporters.Teleporters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Teleporters.getInstance().teleporting.contains(event.getPlayer())) event.setCancelled(true);
    }
}
