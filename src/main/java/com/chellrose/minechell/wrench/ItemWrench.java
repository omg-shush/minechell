package com.chellrose.minechell.wrench;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.chellrose.minechell.Util;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemWrench {
    public static final String WRENCH_NBT_KEY = "caa_wrench";
    public static final Material WRENCH_MATERIAL = Material.TRIPWIRE_HOOK;

    private static ItemStack wrench = null;
    static {
        wrench = new ItemStack(WRENCH_MATERIAL);

        // Set NBT data on wrench
        NBTItem nbt = new NBTItem(wrench);
        nbt.setString(Util.PLUGIN_KEY, WRENCH_NBT_KEY);
        nbt.applyNBT(wrench);
        wrench.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        // Set metadata on wrench
        ItemMeta itemMeta = wrench.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Turbo Encabulator");
        itemMeta.setLocalizedName("Turbo Encabulator");
        itemMeta.setUnbreakable(true);
        itemMeta.setAttributeModifiers(null);

        // Set lore on wrench
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "--------------------------------------------------------------------------------");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "For a number of years now, work has been proceeding in order to bring ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "perfection to the crudely conceived idea of a transmission that would not only ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supply inverse reactive current for use in unilateral phase detractors, but ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "would also be capable of automatically synchronizing cardinal grammeters. ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Such an instrument is the " + ChatColor.BOLD + ChatColor.ITALIC + "Turbo Encabulator" + ChatColor.DARK_AQUA + ".");
        lore.add(ChatColor.GRAY + "--------------------------------------------------------------------------------");
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(42);

        wrench.setItemMeta(itemMeta);
    }

    public ItemWrench(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, WRENCH_NBT_KEY);
        ShapedRecipe recipe = new ShapedRecipe(key, ItemWrench.wrench);
        recipe.shape(" b", "c ");
        recipe.setIngredient('b', Material.END_CRYSTAL);
        recipe.setIngredient('c', Material.END_ROD);
        Bukkit.addRecipe(recipe);
    }

    public static boolean isWrench(ItemStack item) {
        if (item != null) {
            NBTItem nbt = new NBTItem(item);
            return item.getType() == WRENCH_MATERIAL && nbt.hasTag(Util.PLUGIN_KEY) && nbt.getString(Util.PLUGIN_KEY).equals(WRENCH_NBT_KEY);
        }
        return false;
    }
}
