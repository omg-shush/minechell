package com.chellrose.minechell.wrench;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Represents a wrench item in the game.
 * The wrench can be used for various purposes, such as modifying block states.
 */
public class ItemWrench {
    public static final Material WRENCH_MATERIAL = Material.TRIPWIRE_HOOK;
    private Plugin plugin;
    private ItemStack wrench;
    private NamespacedKey isWrenchKey;
    private NamespacedKey wrenchRecipeKey;
    private NamespacedKey blockDataKey;

    public ItemWrench(Plugin plugin) {
        this.plugin = plugin;

        this.isWrenchKey = new NamespacedKey(plugin, "caa_wrench");
        this.wrenchRecipeKey = new NamespacedKey(plugin, "caa_crafting_wrench");
        this.blockDataKey = new NamespacedKey(plugin, "caa_block_data");

        // Item meta
        this.wrench = new ItemStack(WRENCH_MATERIAL);
        ItemMeta wrenchMeta = this.wrench.getItemMeta();

        PersistentDataContainer container = wrenchMeta.getPersistentDataContainer();
        container.set(this.isWrenchKey, PersistentDataType.BOOLEAN, true);

        wrenchMeta.setEnchantmentGlintOverride(true);
        wrenchMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Turbo Encabulator");
        wrenchMeta.setUnbreakable(true);
        wrenchMeta.setAttributeModifiers(null);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "--------------------------------------------------------------------------------");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "For a number of years now, work has been proceeding in order to bring ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "perfection to the crudely conceived idea of a transmission that would not only ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supply inverse reactive current for use in unilateral phase detractors, but ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "would also be capable of automatically synchronizing cardinal grammeters. ");
        lore.add(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Such an instrument is the " + ChatColor.BOLD + ChatColor.ITALIC + "Turbo Encabulator" + ChatColor.DARK_AQUA + ".");
        lore.add(ChatColor.GRAY + "--------------------------------------------------------------------------------");
        wrenchMeta.setLore(lore);
        wrenchMeta.setCustomModelData(42);

        this.wrench.setItemMeta(wrenchMeta);

        // Crafting recipe
        ShapedRecipe recipe = new ShapedRecipe(wrenchRecipeKey, this.wrench);
        recipe.shape(" b", "c ");
        recipe.setIngredient('b', Material.END_CRYSTAL);
        recipe.setIngredient('c', Material.END_ROD);
        Bukkit.addRecipe(recipe);
    }

    public boolean isWrench(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                return container.getOrDefault(this.isWrenchKey, PersistentDataType.BOOLEAN, false);
            }
        }
        return false;
    }

    public void setBlockData(ItemStack wrench, Class<?> clazz, BlockData data) {
        ItemMeta meta = wrench.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer blockDataContainer = container.getOrDefault(
            this.blockDataKey,
            PersistentDataType.TAG_CONTAINER,
            container.getAdapterContext().newPersistentDataContainer());
        blockDataContainer.set(new NamespacedKey(this.plugin, clazz.getName()), PersistentDataType.STRING, data.getAsString());
        container.set(this.blockDataKey, PersistentDataType.TAG_CONTAINER, blockDataContainer); // save if got new container
        wrench.setItemMeta(meta);
    }

    public <T> T getBlockData(ItemStack wrench, Class<T> clazz) {
        ItemMeta meta = wrench.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer blockDataContainer = container.get(this.blockDataKey, PersistentDataType.TAG_CONTAINER);
        if (blockDataContainer != null) {
            String blockData = blockDataContainer.get(new NamespacedKey(this.plugin, clazz.getName()), PersistentDataType.STRING);
            if (blockData != null) {
                try {
                    BlockData data = Bukkit.getServer().createBlockData(blockData);
                    return clazz.cast(data);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
