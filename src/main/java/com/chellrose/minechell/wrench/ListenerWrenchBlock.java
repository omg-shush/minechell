package com.chellrose.minechell.wrench;

import org.bukkit.event.Listener;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerWrenchBlock implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (ItemWrench.isWrench(item)) {
                Block block = event.getClickedBlock();
                BlockData data = block.getBlockData();
                if (data instanceof Stairs) {
                    event.setCancelled(true);
                    Stairs stairs = (Stairs)data;

                    stairs.setHalf(stairs.getHalf() == Stairs.Half.TOP ? Stairs.Half.BOTTOM : Stairs.Half.TOP);
                    block.setBlockData(stairs);
                }
            }
        }
    }
}
