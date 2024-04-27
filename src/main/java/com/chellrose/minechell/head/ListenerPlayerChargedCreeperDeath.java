package com.chellrose.minechell.head;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ListenerPlayerChargedCreeperDeath implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.CREEPER && ((Creeper)event.getDamager()).isPowered()) {
            if (event.getEntityType() == EntityType.PLAYER) {
                Player player = (Player)event.getEntity();
                if (event.getFinalDamage() >= player.getHealth()) {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta headMeta = (SkullMeta)head.getItemMeta();
                    headMeta.setOwningPlayer(player);
                    head.setItemMeta(headMeta);
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), head);
                }
            }
        }
    }
}
