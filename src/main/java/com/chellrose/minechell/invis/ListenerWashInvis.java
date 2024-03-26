package com.chellrose.minechell.invis;

import java.util.Collection;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;

public class ListenerWashInvis implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof ThrownPotion) {
            ThrownPotion potion = (ThrownPotion)event.getEntity();
            if (potion.getEffects().isEmpty()) {
                // Zero effects, is a splash water bottle
                // Imitate vanilla splash potion effect range, since splash water bottles
                // don't normally create SplashPotionEvents
                Location center = potion.getLocation();
                Collection<Entity> inBoundingBox = potion.getWorld().getNearbyEntities(potion.getLocation(), 8.25 / 2, 4.25 / 2, 8.25 / 2);
                for (Entity e : inBoundingBox) { // This includes the splash potion itself, which is fine
                    if (e.getLocation().distanceSquared(center) < 4*4) {
                        // i opened an oreos box and got a bitch ticket
                        if (e instanceof LivingEntity && e.getType() != EntityType.ARMOR_STAND) {
                            // Handle living entities: remove potion effect
                            LivingEntity living = (LivingEntity)e;
                            if (living.getPotionEffect(PotionEffectType.INVISIBILITY) != null) {
                                living.removePotionEffect(PotionEffectType.INVISIBILITY);
                                living.playEffect(EntityEffect.ENTITY_POOF);
                            }
                        } else {
                            // Handle nonliving entities: set visible
                            switch (e.getType()) {
                            case ARMOR_STAND:
                                ArmorStand armorStand = (ArmorStand)e;
                                armorStand.setVisible(true);
                                break;
                            case ITEM_FRAME:
                                ItemFrame itemFrame = (ItemFrame)e;
                                itemFrame.setVisible(true);
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
