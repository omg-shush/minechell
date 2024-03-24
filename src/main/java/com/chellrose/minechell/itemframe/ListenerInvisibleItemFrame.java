package com.chellrose.minechell.itemframe;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerInvisibleItemFrame implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame)event.getRightClicked();
            ItemStack hand = event.getPlayer().getEquipment().getItem(event.getHand());
            boolean itemFrameFilled = frame.getItem().getType() != Material.AIR;
            boolean itemFrameVisible = frame.isVisible();
            boolean hasPhantomMembranes = hand.getType() == Material.PHANTOM_MEMBRANE;
            boolean hasInkSacs = hand.getType() == Material.INK_SAC;
            if (itemFrameFilled && itemFrameVisible && hasPhantomMembranes) {
                hand.setAmount(hand.getAmount() - 1);
                frame.setVisible(false);
                event.setCancelled(true);
            } else if (itemFrameFilled && !itemFrameVisible && hasInkSacs) {
                hand.setAmount(hand.getAmount() - 1);
                frame.setVisible(true);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame)event.getEntity();
            if (!itemFrame.isVisible()) {
                // Cancel the event to prevent the item from being removed in the usual way
                event.setCancelled(true);
                if (itemFrame.getItem() != null && !itemFrame.getItem().getType().equals(Material.AIR)) {
                    itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), itemFrame.getItem());
                }
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), new ItemStack(Material.ITEM_FRAME));
                itemFrame.remove();
            }
        }
    }
}
