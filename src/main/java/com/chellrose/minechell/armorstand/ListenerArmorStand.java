package com.chellrose.minechell.armorstand;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * A listener for extra interactions with Armor Stands.
 */
public class ListenerArmorStand implements Listener {
    private static final Map<Material, ArmorStandPose> musicDiscPoses = new HashMap<>();
    static {
        musicDiscPoses.put(Material.MUSIC_DISC_CAT,     ArmorStandPose.LOOKHANDS_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_BLOCKS,  ArmorStandPose.LOOKATTHIS_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_CHIRP,   ArmorStandPose.PRAY_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_FAR,     ArmorStandPose.HUG_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_MALL,    ArmorStandPose.DAB_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_MELLOHI, ArmorStandPose.BRUH_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_STAL,    ArmorStandPose.DANCE1_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_STRAD,   ArmorStandPose.DANCE2_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_WARD,    ArmorStandPose.PROPOSE_POSE);
        musicDiscPoses.put(Material.MUSIC_DISC_WAIT,    ArmorStandPose.WAIT_POSE);
    }

    /**
     * Performs specific actions when the player uses an item on an Armor Stand.
     *
     * @param event The PlayerInteractAtEntityEvent to handle.
     */
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(event.getHand());
            if (item != null && item.getType() != Material.AIR && player.isSneaking()) {
                switch (item.getType()) {
                    case STICK: // Adds arms to the armor stand
                        if (!armorStand.hasArms() && item.getAmount() >= 2) {
                            event.setCancelled(true); // Would otherwise place stick in armor stand's hand
                            armorStand.setArms(true);
                            item.setAmount(item.getAmount() - 2);
                        }
                        break;
                    case SHEARS: // Removes the arms from the armor stand
                        if (armorStand.hasArms()) {
                            event.setCancelled(true);
                            armorStand.setArms(false);
                            dropHandItems(armorStand);
                            damageItem(item, player);
                        }
                        break;
                    case WOODEN_PICKAXE:
                    case STONE_PICKAXE:
                    case IRON_PICKAXE:
                    case DIAMOND_PICKAXE:
                    case NETHERITE_PICKAXE:
                    case GOLDEN_PICKAXE: // Removes the base plate from the armor stand
                        if (armorStand.hasBasePlate()) {
                            event.setCancelled(true);
                            damageItem(item, player);
                            armorStand.setBasePlate(false);
                        }
                        break;
                    case SMOOTH_STONE_SLAB: // Adds a base plate to the armor stand
                        event.setCancelled(true);
                        if (!armorStand.hasBasePlate()) {
                            item.setAmount(item.getAmount() - 1);
                            armorStand.setBasePlate(true);
                        }
                        break;
                    default:
                        if (item.getType().isRecord()) {
                            // Adjust armor stand pose based on the music disc used
                            event.setCancelled(true);
                            ArmorStandPose pose = musicDiscPoses.getOrDefault(item.getType(), ArmorStandPose.STRAIGHT_POSE);
                            pose.setPose(armorStand);
                        }
                        break;
                }
            }
        }
    }

    /**
     * Drops the items held in the armor stand's hands.
     *
     * @param armorStand The ArmorStand to drop the items from.
     */
    private void dropHandItems(ArmorStand armorStand) {
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
    }

    /**
     * Damages the specified item by 1 durability point.
     * If the item's durability reaches 0, it breaks the item & plays the sound.
     *
     * @param item   The ItemStack to damage.
     * @param player The player holding the item.
     */
    private void damageItem(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            int newDamage = damageable.getDamage() + 1;
            if (newDamage < item.getType().getMaxDurability()) {
                damageable.setDamage(newDamage);
                item.setItemMeta(meta);
            } else {
                // If durability is 0, break the item
                item.setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            }
        }
    }
}
