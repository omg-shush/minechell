package com.chellrose.minechell.armorstand;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Sound;

public class ListenerArmorStand implements Listener {
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand)event.getRightClicked();
            ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
            if (item != null && item.getType() != Material.AIR) {
                if (item.getType() == Material.STICK) {
                    if (!armorStand.hasArms() && item.getAmount() >= 2) {
                        event.setCancelled(true); // Would otherwise place stick in armor stand's hand
                        armorStand.setArms(true);
                        // Consume two sticks
                        item.setAmount(item.getAmount() - 2);
                    }
                } else if (item.getType() == Material.SHEARS) {
                    if (armorStand.hasArms()) {
                        event.setCancelled(true);
                        armorStand.setArms(false);
                        // Drop any items the armor stand may be holding
                        ItemStack mainHandItem = armorStand.getEquipment().getItemInMainHand();
                        if (mainHandItem != null && mainHandItem.getType() != Material.AIR) {
                            armorStand.getWorld().dropItemNaturally(armorStand.getLocation(), mainHandItem);
                            armorStand.getEquipment().setItemInMainHand(null);
                        }
                        ItemStack offHandItem = armorStand.getEquipment().getItemInOffHand();
                        if (offHandItem != null && offHandItem.getType() != Material.AIR) {
                            armorStand.getWorld().dropItemNaturally(armorStand.getLocation(), offHandItem);
                            armorStand.getEquipment().setItemInOffHand(null);
                        }
                        // Damage the shears
                        ItemMeta meta = item.getItemMeta();
                        Damageable damageable = (Damageable)meta;
                        int newDamage = damageable.getDamage() + 1;
                        if (newDamage < item.getType().getMaxDurability()) {
                            damageable.setDamage(newDamage);
                            item.setItemMeta((ItemMeta)damageable);
                        } else {
                            // If durability is 0, break the item
                            item.setAmount(0);
                            event.getPlayer().playSound(armorStand.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }
}
