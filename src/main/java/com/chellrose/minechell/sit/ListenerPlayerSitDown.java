package com.chellrose.minechell.sit;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ListenerPlayerSitDown implements Listener {
    private Set<Material> sittable;

    public ListenerPlayerSitDown() {
        sittable = new HashSet<>();
        for (Material material : Material.values()) {
            if (material.name().contains("STAIRS") || material.name().contains("SLAB")) {
                sittable.add(material);
            }
        }
    }

    private void sit(Player player, Block block) {
        // Spawn entity at center of block
        Location location = block.getLocation().add(0.5, 0.0, 0.5);
        Arrow arrow = player.getWorld().spawnArrow(location, new Vector(1, 0, 0), 0.0f, 0.0f);
        arrow.setDamage(0.0);
        arrow.setInvulnerable(true);
        arrow.setCustomName("sit");
        arrow.setGravity(false);
        arrow.setSilent(true);

        arrow.addPassenger(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getMaterial().isAir()) {
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
            if (arrow.getCustomName().equals("sit")) {
                arrow.remove();
            }
        }
    }
}
