package com.chellrose.minechell.head;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import com.chellrose.minechell.Util;

public class ListenerWanderingTrader implements Listener {
    private Random random;

    public ListenerWanderingTrader() {
        this.random = new Random();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WANDERING_TRADER) {
            // Random chance for a trader to have the head trade
            if (this.random.nextInt(100) < 60) {
                // Random price
                int price = this.random.nextInt(16) + 64 - 15;

                // Add the trade
                WanderingTrader trader = (WanderingTrader)event.getEntity();
                List<MerchantRecipe> recipes = new ArrayList<>(trader.getRecipes());
                MerchantRecipe r = new MerchantRecipe(new ItemStack(Material.PLAYER_HEAD), 16);
                r.addIngredient(new ItemStack(Material.EMERALD, price));
                r.addIngredient(new ItemStack(Material.PLAYER_HEAD, 1));
                recipes.add(r);
                trader.setRecipes(recipes);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            MerchantInventory mi = (MerchantInventory)event.getInventory();
            ItemStack head = this.isHeadTrade(mi);
            if (head != null) {
                if (((SkullMeta)head.getItemMeta()).hasOwner()) {
                    // Allow dirty heads
                    event.setResult(Result.DEFAULT);
                } else if (event.getRawSlot() != 2) {
                    // Allow interacting with other parts of the inventory
                    event.setResult(Result.DEFAULT);
                } else {
                    // Don't allow trading already-clean heads
                    event.setResult(Result.DENY);
                    HumanEntity e = mi.getMerchant().getTrader();
                    if (e != null && e instanceof Player) {
                        Util.sendItalic((Player)e, "That head is already clean!");
                    }
                }
            }
        }
    }

    private ItemStack isHeadTrade(MerchantInventory mi) {
        MerchantRecipe recipe = mi.getSelectedRecipe();
        if (recipe != null && recipe.getResult().getType() == Material.PLAYER_HEAD) {
            ItemStack leftInput = mi.getItem(0);
            ItemStack rightInput = mi.getItem(1);
            if (leftInput.getType() == Material.PLAYER_HEAD) {
                // Inputs may be backwards, swap order
                leftInput = mi.getItem(1);
                rightInput = mi.getItem(0);
            }
            if (leftInput != null && rightInput != null) {
                int requiredEmeralds = recipe.getIngredients().get(0).getAmount();
                if (leftInput.getType() == Material.EMERALD && leftInput.getAmount() >= requiredEmeralds) {
                    if (rightInput.getType() == Material.PLAYER_HEAD && rightInput.getAmount() >= 1) {
                        return rightInput;
                    }
                }
            }
        }
        return null;
    }
}
