package me.gavitsra.teleporters.tasks;

import me.gavitsra.teleporters.Teleporters;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EchoReturn extends BukkitRunnable {
    Player player;
    Location loc;

    public EchoReturn(Player player, Location loc) {
        this.player = player;
        this.loc = loc;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Teleporters.getInstance().teleportPlayerToLocation(loc, player);
            }
        }.runTaskLater(Teleporters.getInstance(), 20 * 60);
        for (int i = 0; i < 20 * 60 / 4 + 40; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc.clone().add(0, 1, 0), 100, .25, .5, .25, .05);
                }
            }.runTaskLater(Teleporters.getInstance(), i * 4);
        }
    }
}
