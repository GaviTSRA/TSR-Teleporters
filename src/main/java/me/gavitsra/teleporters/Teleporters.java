package me.gavitsra.teleporters;

import me.gavitsra.teleporters.listeners.MoveListener;
import me.gavitsra.teleporters.listeners.SneakListener;
import me.gavitsra.teleporters.tasks.Teleport;
import me.gavitsra.teleporters.tasks.TeleportParticles;
import me.gavitsra.teleporters.tasks.Update;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class Teleporters extends JavaPlugin {
    private static Teleporters instance;
    public List<Player> teleporting;
    public HashMap<Player, Location> echoing;
    private NamespacedKey isTeleporter;
    private NamespacedKey isPortable;

    @Override
    public void onEnable() {
        instance = this;
        isTeleporter = new NamespacedKey(instance, "is_teleporter");
        isPortable = new NamespacedKey(Teleporters.getInstance(), "is_portable");
        teleporting = new ArrayList<>();
        echoing = new HashMap<>();

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new SneakListener(), this);
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        new Update().runTaskTimer(this, 0, 5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Teleporters getInstance() {
        return instance;
    }

    public Entity getPlatformByID(String id) {
        List<Entity> nearbyList = new ArrayList<>();
        Stream<Entity> nearby;
        for (World world : Bukkit.getWorlds()) {
            nearbyList.addAll(world.getEntities());
        }
        nearby = nearbyList.stream();

        nearby = nearby.filter(e->e.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE));
        nearby = nearby.filter(e-> Objects.equals(e.getCustomName(), id));

        return nearby.toList().get(0);
    }

    public void teleportPlayerToPlatform(String id, Player player, boolean fast) {
        Entity entityTo = getPlatformByID(id);
        Teleporters.getInstance().teleporting.add(player);

        if (fast) {
            int count = 10;
            while (count > 0) {
                count -= 1;
                new TeleportParticles(player.getWorld(), player.getLocation(), count*200, entityTo.getLocation(), Particle.END_ROD, 2, .01f).runTaskLater(Teleporters.getInstance(), (10 - count));
                new TeleportParticles(player.getWorld(), entityTo.getLocation(), count*200, player.getLocation(), Particle.END_ROD, 2, .01f).runTaskLater(Teleporters.getInstance(), (10 - count));
            }
            new Teleport(player, entityTo.getLocation()).runTaskLater(Teleporters.getInstance(), 10);
        } else {
            int count = 1000;
            while (count > 0) {
                count -= 1;
                new TeleportParticles(player.getWorld(), player.getLocation(), count, entityTo.getLocation(), Particle.TOTEM, 0, 0).runTaskLater(Teleporters.getInstance(), (1000 - count)/10L);
                new TeleportParticles(player.getWorld(), entityTo.getLocation(), count, player.getLocation(), Particle.TOTEM , 0, 0).runTaskLater(Teleporters.getInstance(), (1000 - count)/10L);
            }
            new Teleport(player, entityTo.getLocation()).runTaskLater(Teleporters.getInstance(), 100);
        }
        handlePortablePlatformRemoval(entityTo);
    }

    public void teleportPlayerToLocation(Location loc, Player player) {
        Teleporters.getInstance().teleporting.add(player);

        int count = 1000;
        while (count > 0) {
            count -= 1;
            new TeleportParticles(player.getWorld(), player.getLocation(), count, loc, Particle.TOTEM, 0, 0).runTaskLater(Teleporters.getInstance(), (1000 - count)/10L);
            new TeleportParticles(player.getWorld(), loc, count, player.getLocation(), Particle.TOTEM, 0, 0).runTaskLater(Teleporters.getInstance(), (1000 - count)/10L);
        }
        new Teleport(player, loc).runTaskLater(Teleporters.getInstance(), 100);
    }

    public void handlePortablePlatformRemoval(Entity entityTo) {
        if (!entityTo.getPersistentDataContainer().has(isPortable, PersistentDataType.BYTE)) return;
        if (entityTo.getPersistentDataContainer().get(isPortable, PersistentDataType.BYTE) != (byte) 1) return;

        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(entityTo.getCustomName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Portable Platform");
        meta.setLore(lore);
        item.setItemMeta(meta);
        entityTo.getWorld().dropItem(entityTo.getLocation(), item);
        entityTo.remove();
    }
}
