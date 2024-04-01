package com.chellrose.minechell.carry;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.chellrose.minechell.Util;

public class ListenerPlayerCarry implements Listener {
    public static final double EJECTION_SPEED = 1.0;

    /**
     * If a player right-clicks another player/armor stand with a saddle
     * in their helmet slot, attempts to ride the player/armor stand.
     *
     * @param event The PlayerInteractAtEntityEvent.
     */
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!Util.hasItem(player.getEquipment().getItemInMainHand())) {
            Entity target = event.getRightClicked();
            if (target instanceof Player || target instanceof ArmorStand) {
                LivingEntity carrier = (LivingEntity)target;
                ItemStack helmet = carrier.getEquipment().getHelmet();
                if (helmet != null && helmet.getType() == Material.SADDLE) {
                    // Attempt to ride the carrier
                    if (!player.isInsideVehicle() && carrier.getPassengers().isEmpty()) {
                        carrier.addPassenger(player);
                    }
                }
            }
        }
    }

    /**
     * Checks if the player left-clicks in the air while sneaking and ejects any passengers in the direction the player is looking.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            // Eject any passengers in the direction the player is looking
            List<Entity> passengers = player.getPassengers();
            if (player.eject()) {
                for (Entity passenger : passengers) {
                    passenger.setVelocity(player.getEyeLocation().getDirection().multiply(EJECTION_SPEED));
                }
            }
        }
    }
}
