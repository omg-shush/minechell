package com.chellrose.minechell.wrench;

import org.bukkit.event.Listener;
import org.bukkit.EntityEffect;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.chellrose.minechell.armorstand.ArmorStandPose;

public class ListenerWrenchArmorStand implements Listener {
    private ItemWrench itemWrench;

    public ListenerWrenchArmorStand(ItemWrench itemWrench) {
        this.itemWrench = itemWrench;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.HAND && player.isSneaking() && event.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand)event.getRightClicked();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (this.itemWrench.isWrench(itemInHand)) {
                // Cycle to next armor stand pose
                ArmorStandPose pose = new ArmorStandPose(armorStand);
                int newIndex = pose.index() + 1;
                ArmorStandPose newPose = ArmorStandPose.fromIndex(newIndex);
                newPose.apply(armorStand);

                event.setCancelled(true);
                armorStand.playEffect(EntityEffect.ARMOR_STAND_HIT);
            }
        }
    }
}
