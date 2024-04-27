package com.chellrose.minechell.wrench;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerItemWrench implements Listener {
    private ItemWrench itemWrench;

    public ListenerItemWrench(ItemWrench itemWrench) {
        this.itemWrench = itemWrench;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item != null && this.itemWrench.isWrench(item)) {
            event.setCancelled(true);
        }
    }
}
