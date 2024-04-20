package com.chellrose.minechell;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Util {
    public static final String PLUGIN_KEY = "minechell";
    public static final String ITEM_KEY = "caa_item";

    public static void sendItalic(Player player, String message) {
        BaseComponent c = new TextComponent(message);
        c.setItalic(true);
        player.spigot().sendMessage(c);
    }

    /**
     * Checks if the given ItemStack has a valid item.
     *
     * @param item The ItemStack to check.
     * @return true if the ItemStack is not null and has a valid item, false otherwise.
     */
    public static boolean hasItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    public static boolean armorStandHasItem(ArmorStand armorStand) {
        return armorStandItemCount(armorStand) > 0;
    }

    public static int armorStandItemCount(ArmorStand armorStand) {
        int countHelmet = hasItem(armorStand.getEquipment().getHelmet()) ? 1 : 0;
        int countChestplate = hasItem(armorStand.getEquipment().getChestplate()) ? 1 : 0;
        int countLeggings = hasItem(armorStand.getEquipment().getLeggings()) ? 1 : 0;
        int countBoots = hasItem(armorStand.getEquipment().getBoots()) ? 1 : 0;
        int countItemInHand = hasItem(armorStand.getEquipment().getItemInMainHand()) ? 1 : 0;
        int countItemInOffHand = hasItem(armorStand.getEquipment().getItemInOffHand()) ? 1 : 0;
        return countHelmet + countChestplate + countLeggings + countBoots + countItemInHand + countItemInOffHand;
    }
}
