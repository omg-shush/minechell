package com.chellrose.minechell.sit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ListenerPlayerSitDown implements Listener {
    public static final double DELTA_Y = -0.15;

    private Plugin plugin;
    private Set<Material> sittable;
    private Set<Material> signs;
    private HashMap<UUID, Long> lastSit;
    private HashMap<Integer, BukkitTask> keepaliveTasks;

    public ListenerPlayerSitDown(Plugin plugin) {
        this.plugin = plugin;
        this.sittable = new HashSet<>();
        this.signs = new HashSet<>();
        this.lastSit = new HashMap<>();
        this.keepaliveTasks = new HashMap<>();
        for (Material material : Material.values()) {
            String name = material.name();
            if (name.contains("STAIRS") || name.contains("SLAB")) {
                this.sittable.add(material);
            } else if (name.contains("SIGN")) {
                this.signs.add(material);
            }
        }
    }

    private boolean sit(Player player, Block block) {
        // Spawn entity at center of block
        Location location = block.getLocation().add(0.5, DELTA_Y, 0.5);
        Arrow arrow = player.getWorld().spawnArrow(location, new Vector(0, 1, 0), 0.0f, 0.0f);
        arrow.setDamage(0.0);
        arrow.setVelocity(new Vector(0, DELTA_Y, 0));
        arrow.setInvulnerable(true);
        arrow.setGravity(false);
        arrow.setSilent(true);
        arrow.setPickupStatus(PickupStatus.DISALLOWED);
        this.makeSitArrow(arrow);
        Block upperBlock = player.getWorld().getBlockAt(block.getLocation().add(0.0, 1.0, 0.0));
        if (upperBlock.getType().isOccluding() || upperBlock.getBlockData() instanceof Slab && ((Slab)upperBlock.getBlockData()).getType() == Type.DOUBLE) {
            arrow.remove();
            return false;
        }
        Block[] adjacentBlocks = new Block[]{
            block.getRelative(BlockFace.NORTH),
            block.getRelative(BlockFace.EAST),
            block.getRelative(BlockFace.SOUTH),
            block.getRelative(BlockFace.WEST)
        };
        boolean isChair = false;
        for (Block adjacent : adjacentBlocks) {
            if (adjacent.getBlockData() instanceof TrapDoor || this.signs.contains(adjacent.getType())) {
                isChair = true;
                break;
            }
        }
        if (!isChair) {
            arrow.remove();
            return false;
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!arrow.isDead() && arrow.isInBlock() && !player.isDead() && player.isOnline()) {
                arrow.addPassenger(player);
                BukkitTask keepalive = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
                    Block blockAttached = arrow.getAttachedBlock();
                    Block blockAbove = blockAttached == null ? null : blockAttached.getRelative(BlockFace.UP);
                    BlockData blockData = blockAbove == null ? null : blockAbove.getBlockData();
                    if (!arrow.isDead() && !arrow.getPassengers().isEmpty() && arrow.isInBlock() && this.isValidSeat(blockData)) {
                        arrow.setTicksLived(1);
                    } else {
                        arrow.remove();
                        BukkitTask task = this.keepaliveTasks.remove(arrow.getEntityId());
                        if (task != null) {
                            task.cancel();
                        }
                    }
                }, 5, 5);
                this.keepaliveTasks.put(arrow.getEntityId(), keepalive);
            } else {
                arrow.remove();
            }
        }, 2);
        return true;
    }

    private boolean isSitArrow(Arrow arrow) {
        if (arrow != null && arrow.hasMetadata("sit")) {
            List<MetadataValue> meta = arrow.getMetadata("sit");
            return !meta.isEmpty() && meta.get(0).asBoolean() && meta.get(0).getOwningPlugin() == this.plugin;
        } else {
            return false;
        }
    }

    private void makeSitArrow(Arrow arrow) {
        arrow.setMetadata("sit", new FixedMetadataValue(this.plugin, true));
    }

    private boolean isValidSeat(BlockData blockData) {
        return (blockData instanceof Slab && ((Slab)blockData).getType() == Slab.Type.BOTTOM) ||
            (blockData instanceof Stairs && ((Stairs)blockData).getHalf() == Half.BOTTOM) ||
            (blockData instanceof Campfire && !((Campfire)blockData).isLit());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            event.getMaterial().isAir() &&
            event.getClickedBlock() != null &&
            event.getBlockFace() == BlockFace.UP
        ) {
            // Right click with empty hand
            Block block = event.getClickedBlock();
            if (this.sittable.contains(block.getType())) {
                if (this.isValidSeat(block.getBlockData())) {
                    Player player = event.getPlayer();
                    UUID uuid = player.getUniqueId();
                    long currentTime = System.currentTimeMillis();
                    if (!this.lastSit.containsKey(uuid) || currentTime - this.lastSit.get(uuid) > 1_000) {
                        if (this.sit(player, block)) {
                            this.lastSit.put(uuid, currentTime);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getDismounted() instanceof Arrow) {
            Arrow arrow = (Arrow)event.getDismounted();
            if (this.isSitArrow(arrow)) {
                arrow.remove();
                BukkitTask keepalive = this.keepaliveTasks.remove(arrow.getEntityId());
                if (keepalive != null) {
                    keepalive.cancel();
                }
                Player player = (Player)event.getEntity();
                Location standLocation = player.getLocation();
                standLocation.setX(standLocation.getBlockX() + 0.5);
                standLocation.setY(standLocation.getBlockY() + 2.0);
                standLocation.setZ(standLocation.getBlockZ() + 0.5);
                if (!player.isDead() && player.getVehicle() == null) {
                    player.teleport(standLocation);
                }
            }
        }
    }

    public void handleDisable() {
        this.plugin.getLogger().info("Removing all sit arrows...");
        for (World world : this.plugin.getServer().getWorlds()) {
            for (Arrow arrow : world.getEntitiesByClass(Arrow.class)) {
                if (this.isSitArrow(arrow)) {
                    arrow.remove();
                }
            }
        }
    }
}
