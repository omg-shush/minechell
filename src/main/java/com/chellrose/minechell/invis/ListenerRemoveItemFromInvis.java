package com.chellrose.minechell.invis;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import com.chellrose.minechell.Util;

public class ListenerRemoveItemFromInvis implements Listener {
    // If an item frame is invisible and its item is being removed,
    // then break the item frame.
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME && event.getDamager().getType() == EntityType.PLAYER) {
            ItemFrame itemFrame = (ItemFrame)event.getEntity();
            if (!itemFrame.isVisible() && itemFrame.getItem() != null && itemFrame.getItem().getType() != Material.AIR) {
                event.setCancelled(true);
                itemFrame.remove();
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), new ItemStack(Material.ITEM_FRAME));
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), itemFrame.getItem());
            }
        }
    }

    // If an armor stand is invisible and its last item is being removed,
    // then break the armor stand.
    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        boolean isInvis = !event.getRightClicked().isVisible();
        boolean isLastItem = Util.armorStandItemCount(event.getRightClicked()) <= 1;
        boolean isBeingRemoved = Util.hasItem(event.getArmorStandItem()) && !Util.hasItem(event.getPlayerItem());
        if (isInvis && isLastItem && isBeingRemoved) {
            // Disallow empty invis armor stands. Drop the armor stand.
            ArmorStand armorStand = event.getRightClicked();
            armorStand.remove();
            armorStand.getWorld().dropItemNaturally(armorStand.getLocation(), new ItemStack(Material.ARMOR_STAND));
        }
    }
}
