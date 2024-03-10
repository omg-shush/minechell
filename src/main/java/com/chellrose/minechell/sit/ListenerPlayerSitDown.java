package com.chellrose.minechell.sit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ListenerPlayerSitDown implements Listener {
    public static final double DELTA_Y = -0.15;

    private Plugin plugin;
    private Set<Material> sittable;

    public ListenerPlayerSitDown(Plugin plugin) {
        this.plugin = plugin;
        this.sittable = new HashSet<>();
        for (Material material : Material.values()) {
            if (material.name().contains("STAIRS") || material.name().contains("SLAB")) {
                sittable.add(material);
            }
        }
    }

    private void sit(Player player, Block block) {
        // Spawn entity at center of block
        Location location = block.getLocation().add(0.5, DELTA_Y, 0.5);
        Arrow arrow = player.getWorld().spawnArrow(location, new Vector(0, 1, 0), 0.0f, 0.0f);
        arrow.setDamage(0.0);
        arrow.setVelocity(new Vector(0, DELTA_Y, 0));
        arrow.setInvulnerable(true);
        arrow.setGravity(false);
        arrow.setSilent(true);
        this.makeSitArrow(arrow);
        arrow.addPassenger(player);
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
                BlockData blockData = block.getBlockData();
                if ((blockData instanceof Slab && ((Slab)blockData).getType() == Slab.Type.BOTTOM) ||
                    (blockData instanceof Stairs && ((Stairs)blockData).getHalf() == Half.BOTTOM))
                {
                    this.sit(event.getPlayer(), block);
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
                Player player = (Player)event.getEntity();
                Location standLocation = player.getLocation().add(0.0, -1.0 * DELTA_Y, 0.0);
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
