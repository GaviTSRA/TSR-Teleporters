package me.gavitsra.teleporters.tasks;

import me.gavitsra.teleporters.Teleporters;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Update extends BukkitRunnable {
    private final NamespacedKey isTeleporter = new NamespacedKey(Teleporters.getInstance(), "is_teleporter");
    private final NamespacedKey teleportTo = new NamespacedKey(Teleporters.getInstance(), "teleport_to");

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Item entity : world.getEntitiesByClass(Item.class)) {
                handleItemCreation(
                        world, entity,
                        "rename this to the id you want", Material.AMETHYST_SHARD, ChatColor.GOLD + "Activator",
                        Material.AMETHYST_SHARD, Material.DIAMOND, null,
                        Particle.ENCHANTMENT_TABLE, Sound.BLOCK_ENCHANTMENT_TABLE_USE
                );
                handleItemCreation(
                        world, entity,
                        "rename this to the id you want to teleport to", Material.EMERALD, ChatColor.GOLD + "Linker",
                        Material.AMETHYST_SHARD, Material.EMERALD, null,
                        Particle.ENCHANTMENT_TABLE, Sound.BLOCK_ENCHANTMENT_TABLE_USE
                );
                handleItemCreation(
                        world, entity,
                        "rename this to the platform you want to teleport to", Material.AMETHYST_SHARD, ChatColor.GOLD + "Portable Teleporter",
                        Material.AMETHYST_SHARD, Material.COMPASS, null,
                        Particle.ENCHANTMENT_TABLE, Sound.BLOCK_ENCHANTMENT_TABLE_USE
                );
                handleItemCreation(
                        world, entity,
                        ChatColor.DARK_AQUA + "Echoing Core", Material.ECHO_SHARD, ChatColor.DARK_AQUA + "Echoing Core",
                        Material.ECHO_SHARD, Material.DIAMOND_BLOCK, null,
                        Particle.CRIT_MAGIC, Sound.BLOCK_ENCHANTMENT_TABLE_USE
                );
                handleItemCreation(
                        world, entity,
                        "rename this to the platform you want to echo to", Material.ECHO_SHARD, ChatColor.DARK_AQUA + "Echoing Teleporter",
                        Material.ECHO_SHARD, Material.AMETHYST_SHARD, ChatColor.DARK_AQUA + "Echoing Core",
                        Particle.END_ROD, Sound.BLOCK_ENCHANTMENT_TABLE_USE
                );
                handlePlatformCreation(world, entity);
                handlePlatformLinking(world, entity);
            }
            for (Entity entity : world.getEntities().stream().filter(e -> e.getType() == EntityType.MARKER).toList()) {
                if (entity.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE)) {
                    Location loc = entity.getLocation();
                    loc = new Location(world, loc.getBlockX() + .5, loc.getBlockY() + .5, loc.getBlockZ() + .5);
                    if(
                        loc.add(-1, -1, -1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(0, 0, 1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(-1, 0, 0).getBlock().getType() == Material.EMERALD_BLOCK &&
                        loc.add(-1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(0, 0, 1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                        loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK
                    ) {
                        loc.add(-1, 1, -1);
                        if(!entity.getPersistentDataContainer().has(teleportTo, PersistentDataType.STRING)) {
                            world.spawnParticle(Particle.TOTEM, loc, 15, .0, 0, .0, .4);
                        } else {
                            world.spawnParticle(Particle.REVERSE_PORTAL, loc, 40, .0, 0, .0, .05);
                        }
                    } else {
                        loc = entity.getLocation().add(0, 2, 0);
                        world.spawnParticle(Particle.EXPLOSION_LARGE, loc, 5, .2, .2, .2);
                        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        entity.remove();
                    }
                }
            }
        }
    }

    private void handleItemCreation(World world, Item entity, String resultItemName, Material resultItemMaterial, String resultItemLore, Material mat1, Material mat2, String mat1itemLore, Particle particle, Sound sound) {
        Stream<Entity> nearby = entity.getNearbyEntities(3, 3, 3).stream().filter(entity1 -> entity1.getType() == EntityType.DROPPED_ITEM);

        if (entity.getItemStack().getType() == mat1 && entity.getLocation().getBlock().getType() == Material.ENCHANTING_TABLE) {
            for (Entity _other : nearby.toList()) {
                Item other = (Item) _other;

                if (other.getItemStack().getType() == mat2) {
                    ItemStack stack = entity.getItemStack();

                    if (mat1itemLore != null) {
                        if (!stack.hasItemMeta()) return;
                        if (!stack.getItemMeta().hasLore()) return;
                        stack = entity.getItemStack();
                        if (!Objects.equals(stack.getItemMeta().getLore().get(0), mat1itemLore)) return;
                    }

                    if (stack.getAmount()-1 > 0) {
                        entity.getItemStack().setAmount(stack.getAmount()-1);
                    } else {
                        entity.remove();
                    }
                    stack = other.getItemStack();
                    if (stack.getAmount()-1 > 0) {
                        other.getItemStack().setAmount(stack.getAmount()-1);
                    } else {
                        other.remove();
                    }

                    ItemStack item = new ItemStack(resultItemMaterial);
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(resultItemLore);
                    if (Objects.equals(resultItemLore, ChatColor.GOLD + "Portable Teleporter")) {
                        lore.add(ChatColor.BLUE + Integer.toString(5) + ChatColor.GREEN + " uses left");
                    }
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setLore(lore);
                    meta.setDisplayName(resultItemName);
                    item.setItemMeta(meta);
                    world.dropItem(entity.getLocation(), item);

                    world.spawnParticle(particle, entity.getLocation().add(0, 1, 0), 1000, 0, 0, 0, 2);
                    world.playSound(entity.getLocation(), sound, 1, 1f);
                }
            }
        }
    }

    private void handlePlatformCreation(World world, Item entity) {
        if (!entity.getItemStack().hasItemMeta()) return;
        if (!Objects.requireNonNull(entity.getItemStack().getItemMeta()).hasLore()) return;

        List<String> lore = entity.getItemStack().getItemMeta().getLore();
        if (lore == null) return;

        Stream<Entity> nearby = entity.getNearbyEntities(3, 3, 3).stream().filter(entity1 -> entity1.getType() == EntityType.MARKER);
        nearby = nearby.filter(entity1 -> entity1.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE));
        if (nearby.findAny().isPresent()) return;

        if (Objects.equals(lore.get(0), ChatColor.GOLD + "Activator")) {
            Location loc = entity.getLocation();
            if(
                            loc.add(-1, -1, -1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(0, 0, 1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(-1, 0, 0).getBlock().getType() == Material.EMERALD_BLOCK &&
                            loc.add(-1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(0, 0, 1).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK &&
                            loc.add(1, 0, 0).getBlock().getType() == Material.AMETHYST_BLOCK
            ) {
                loc.add(-1, 1, -1);
                if (entity.getItemStack().getItemMeta().getDisplayName().equals("rename this to the id you want")) {
                    world.playSound(loc, Sound.ITEM_SHIELD_BLOCK, .5f, 1);
                    entity.setVelocity(new Vector(0, .3, .3));
                    return;
                }

                String id = entity.getItemStack().getItemMeta().getDisplayName();

                for (World world2 : Bukkit.getWorlds()) {
                    for (Entity entity1 : world2.getEntities().stream().filter(e -> e.getType() == EntityType.MARKER).toList()) {
                        if (Objects.equals(entity1.getCustomName(), id)) {
                            world.playSound(loc, Sound.ITEM_SHIELD_BLOCK, .5f, 1);
                            entity.setVelocity(new Vector(0, .3, .3));
                            return;
                        }
                    }
                }

                EntityType e = EntityType.MARKER;
                Entity res = world.spawnEntity(loc, e);
                res.setCustomNameVisible(false);
                res.setCustomName(id);
                res.getPersistentDataContainer().set(isTeleporter, PersistentDataType.BYTE, (byte) 1);

                loc.getChunk().setForceLoaded(true);

                world.spawnParticle(Particle.END_ROD, loc, 250, 0, 0, 0, .25);
                world.playSound(entity.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1f);

                ItemStack stack = entity.getItemStack();
                if (stack.getAmount()-1 > 0) {
                    entity.getItemStack().setAmount(stack.getAmount()-1);
                } else {
                    entity.remove();
                }
            }
        }
    }

    private void handlePlatformLinking(World world, Item entity) {
        if (!entity.getItemStack().hasItemMeta()) return;
        if (!Objects.requireNonNull(entity.getItemStack().getItemMeta()).hasLore()) return;

        List<String> lore = entity.getItemStack().getItemMeta().getLore();
        if (lore == null) return;

        Stream<Entity> nearby = entity.getNearbyEntities(3, 3, 3).stream().filter(entity1 -> entity1.getType() == EntityType.MARKER);
        nearby = nearby.filter(entity1 -> entity1.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE));
        if (nearby.findAny().isEmpty()) return;

        if (Objects.equals(lore.get(0), ChatColor.GOLD + "Linker")) {
            Location loc = entity.getLocation();
            if (entity.getItemStack().getItemMeta().getDisplayName().equals("rename this to the id you want to teleport to")) {
                world.playSound(loc, Sound.ITEM_SHIELD_BLOCK, .5f, 1);
                entity.setVelocity(new Vector(0, .3, .3));
                return;
            }

            String id = entity.getItemStack().getItemMeta().getDisplayName();

            nearby = world.getEntities().stream().filter(e -> e.getType()==EntityType.MARKER);
            nearby = nearby.filter(e->e.getPersistentDataContainer().has(isTeleporter, PersistentDataType.BYTE));
            nearby = nearby.filter(e->e.getLocation().distance(entity.getLocation()) < 2);
            List<Entity> entities = nearby.toList();
            if (!(entities.size() == 1)) {
                return;
            }

            Entity e = entities.get(0);
            e.getPersistentDataContainer().set(teleportTo, PersistentDataType.STRING, id);
            world.spawnParticle(Particle.PORTAL, entity.getLocation(), 1000, 0, 0, 0, .5);
            world.playSound(entity.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);

            ItemStack stack = entity.getItemStack();
            if (stack.getAmount()-1 > 0) {
                entity.getItemStack().setAmount(stack.getAmount()-1);
            } else {
                entity.remove();
            }
        }
    }
}
