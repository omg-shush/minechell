package com.chellrose.minechell.wrench;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.chellrose.minechell.Util;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;

/**
 * Represents a wrench item in the game.
 * The wrench can be used for various purposes, such as modifying block states.
 */
/**
 * Represents a wrench item in the game.
 * The wrench is used for various purposes, such as modifying block data.
 */
public class ItemWrench {
    public static final String WRENCH_NBT_KEY = "caa_wrench";
    public static final String WRENCH_RECIPE_KEY = "caa_crafting_wrench";
    public static final String BLOCK_DATA_NBT_KEY = "block_data";
    public static final Material WRENCH_MATERIAL = Material.TRIPWIRE_HOOK;

    private static ItemStack WRENCH = null;
    static {
        WRENCH = new ItemStack(WRENCH_MATERIAL);

        // Set NBT data on wrench
        NBTItem nbt = new NBTItem(WRENCH);
        NBTCompound pluginNbt = nbt.addCompound(Util.PLUGIN_KEY);
        pluginNbt.setString(Util.ITEM_KEY, WRENCH_NBT_KEY);
        nbt.applyNBT(WRENCH);
        WRENCH.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        // Set metadata on wrench
        ItemMeta itemMeta = WRENCH.getItemMeta();
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

        WRENCH.setItemMeta(itemMeta);
    }

    public ItemWrench(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, WRENCH_RECIPE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(key, ItemWrench.WRENCH);
        recipe.shape(" b", "c ");
        recipe.setIngredient('b', Material.END_CRYSTAL);
        recipe.setIngredient('c', Material.END_ROD);
        Bukkit.addRecipe(recipe);
    }

    public static boolean isWrench(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
            NBTItem nbt = new NBTItem(item);
            return item.getType() == WRENCH_MATERIAL && nbt.hasTag(Util.PLUGIN_KEY) &&
                nbt.getCompound(Util.PLUGIN_KEY) != null && nbt.getCompound(Util.PLUGIN_KEY).getString(Util.ITEM_KEY) != null &&
                nbt.getCompound(Util.PLUGIN_KEY).getString(Util.ITEM_KEY).equals(WRENCH_NBT_KEY);
        }
        return false;
    }

    public static void setBlockData(ItemStack wrench, Class<?> clazz, BlockData data) {
        NBTItem nbt = new NBTItem(wrench);
        NBTCompound blockDataNbt = nbt.getCompound(Util.PLUGIN_KEY).addCompound(BLOCK_DATA_NBT_KEY); // Create if not exists
        blockDataNbt.setString(clazz.getName(), data.getAsString());
        nbt.applyNBT(wrench);
    }

    public static <T> T getBlockData(ItemStack wrench, Class<T> clazz) {
        NBTItem nbt = new NBTItem(wrench);
        NBTCompound blockDataNbt = nbt.getCompound(Util.PLUGIN_KEY).getCompound(BLOCK_DATA_NBT_KEY);
        if (blockDataNbt != null) {
            String strData = blockDataNbt.getString(clazz.getName());
            if (strData != null) {
                try {
                    BlockData data = Bukkit.getServer().createBlockData(strData);
                    return clazz.cast(data);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
