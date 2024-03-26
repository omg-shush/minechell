package com.chellrose.minechell.invis;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionType;

import com.chellrose.minechell.Util;

public class ListenerMakeInvis implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow)event.getEntity();
            if (arrow.getBasePotionData().getType() == PotionType.INVISIBILITY) {
                Entity hit = event.getHitEntity();
                if (hit != null) {
                    switch (hit.getType()) {
                    case ARMOR_STAND:
                        ArmorStand armorStand = (ArmorStand)hit;
                        if (armorStand.isVisible() && Util.armorStandHasItem(armorStand)) {
                            // Make armor stand invis
                            armorStand.setVisible(false);
                            event.setCancelled(true);
                            arrow.remove();
                        }
                        break;
                    case ITEM_FRAME:
                        ItemFrame itemFrame = (ItemFrame)hit;
                        if (itemFrame.isVisible() && Util.hasItem(itemFrame.getItem())) {
                            // Make item frame invis
                            itemFrame.setVisible(false);
                            event.setCancelled(true);
                            arrow.remove();
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        }
    }
}
