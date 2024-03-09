package com.chellrose.minechell.head;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ListenerPlayerChargedCreeperDeath implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.CREEPER && ((Creeper)event.getDamager()).isPowered()) {
            if (event.getEntityType() == EntityType.PLAYER) {
                Player player = (Player)event.getEntity();
                if (event.getFinalDamage() >= player.getHealth()) {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    NBTItem nbt = new NBTItem(head);
                    CommandHead.applyUUID(player.getUniqueId(), nbt);
                    head = nbt.getItem();
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), head);
                }
            }
        }
    }
}
