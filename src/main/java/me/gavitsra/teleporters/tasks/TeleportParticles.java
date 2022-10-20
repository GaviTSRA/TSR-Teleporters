package me.gavitsra.teleporters.tasks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TeleportParticles extends BukkitRunnable {
    World world;
    Location loc;
    int distance;
    Location locTo;

    public TeleportParticles(World world, Location loc, int distance, Location to) {
        this.world = world;
        this.loc = loc;
        this.distance = distance;
        this.locTo = to;
    }

    @Override
    public void run() {
        List<Location> locations = new ArrayList<>();

        locations.add(loc.clone().add(-distance / 100f, -distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(-distance / 100f, -distance / 100f, 0));
        locations.add(loc.clone().add(-distance / 100f, -distance / 100f, distance / 100f));
        locations.add(loc.clone().add(0, -distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(0, -distance / 100f, 0));
        locations.add(loc.clone().add(0, -distance / 100f, distance / 100f));
        locations.add(loc.clone().add(distance / 100f, -distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(distance / 100f, -distance / 100f, 0));
        locations.add(loc.clone().add(distance / 100f, -distance / 100f, distance / 100f));

        locations.add(loc.clone().add(-distance / 100f, 0, -distance / 100f));
        locations.add(loc.clone().add(-distance / 100f, 0, 0));
        locations.add(loc.clone().add(-distance / 100f, 0, distance / 100f));
        locations.add(loc.clone().add(0, 0, -distance / 100f));
        locations.add(loc.clone().add(0, 0, 0));
        locations.add(loc.clone().add(0, 0, distance / 100f));
        locations.add(loc.clone().add(distance / 100f, 0, -distance / 100f));
        locations.add(loc.clone().add(distance / 100f, 0, 0));
        locations.add(loc.clone().add(distance / 100f, 0, distance / 100f));

        locations.add(loc.clone().add(-distance / 100f, distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(-distance / 100f, distance / 100f, 0));
        locations.add(loc.clone().add(-distance / 100f, distance / 100f, distance / 100f));
        locations.add(loc.clone().add(0, distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(0, distance / 100f, 0));
        locations.add(loc.clone().add(0, distance / 100f, distance / 100f));
        locations.add(loc.clone().add(distance / 100f, distance / 100f, -distance / 100f));
        locations.add(loc.clone().add(distance / 100f, distance / 100f, 0));
        locations.add(loc.clone().add(distance / 100f, distance / 100f, distance / 100f));

        for (Location loc : locations) {
            world.spawnParticle(Particle.TOTEM, loc, 3, 0, 0, 0, 0);
        }

        locations.clear();
        Vector vec = loc.toVector().subtract(locTo.toVector());
        Vector vec2 = locTo.toVector().subtract(loc.toVector());
        for (int i = 0; i < 10; i++) {

            Vector clone = vec2.clone().normalize().multiply(.5);
            Location locClone = loc.clone();
            for (int x = 0; x < i*i/2; x++)
                locClone.add(clone);
            if (locClone.distance(loc) < locClone.distance(locTo)) {
                locations.add(locClone);
            }

            Vector clone2 = vec.clone().normalize().multiply(.5);
            Location locClone2 = locTo.clone();
            for (int x = 0; x < i*i/2; x++)
                locClone2.add(clone2);
            if (locClone2.distance(locTo) < locClone2.distance(loc)) {
                locations.add(locClone2);
            }
        }
        for (Location _loc : locations) {
            if (distance < 5) world.spawnParticle(Particle.FLAME, _loc, 5, 0, 0, 0, 0);
            else world.spawnParticle(Particle.END_ROD, _loc, 5, 0, 0, 0, 0);
        }
    }
}
