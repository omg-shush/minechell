package com.chellrose.minechell.tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerProjectileTag implements Listener {
    private StateMachineTag sm;

    public ListenerProjectileTag(StateMachineTag sm) {
        this.sm = sm;
    }

    private boolean handheldItemForProjectile(ItemStack item, Projectile projectile) {
        return
            (projectile instanceof Arrow && (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW)) ||
            (projectile instanceof Firework && item.getType() == Material.CROSSBOW);
    }

    private void projectileTag(Projectile projectile, @Nonnull Player attacker, @Nullable Player defender) {
        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (projectile instanceof ThrowableProjectile) {
            hand = ((ThrowableProjectile)projectile).getItem();
        } else if (!handheldItemForProjectile(hand, projectile)) {
            hand = attacker.getInventory().getItemInOffHand();
            if (!handheldItemForProjectile(hand, projectile)) {
                hand = null;
            }
        }
        if (hand != null) {
            hand.setAmount(1);
        }
        this.sm.tagWith(defender, attacker, hand);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player && event.getHitEntity() != null) {
            Player attacker = (Player)projectile.getShooter();
            if (sm.store.isJoinedPlayer(attacker.getUniqueId()) && attacker.getUniqueId().equals(this.sm.store.getTaggedPlayer())) {
                // Attacker is it
                if (event.getHitEntity() instanceof Player) {
                    Player defender = (Player)event.getHitEntity();
                    if (sm.store.isJoinedPlayer(defender.getUniqueId())) {
                        // Tag successful
                        this.projectileTag(projectile, attacker, defender);
                    }
                } else if (event.getHitEntity() instanceof ArmorStand) {
                    // Dummy tag
                    this.projectileTag(projectile, attacker, null);
                }
            }
        }
    }
}
