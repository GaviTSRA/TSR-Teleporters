package me.gavitsra.teleporters.tasks;

import me.gavitsra.teleporters.Teleporters;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Teleport extends BukkitRunnable {
    Player player;
    Location loc;

    public Teleport(Player player, Location loc) {
        this.player = player;
        this.loc = loc;
    }

    @Override
    public void run() {
        Teleporters.getInstance().teleporting.remove(player);
        player.teleport(loc);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_TELEPORT, 1, 1);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100, 0, 0, 0, .5);
    }
}
