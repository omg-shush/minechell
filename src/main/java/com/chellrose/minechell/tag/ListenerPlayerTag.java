package com.chellrose.minechell.tag;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ListenerPlayerTag implements Listener {
    private StateMachineTag sm;

    public ListenerPlayerTag(StateMachineTag sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player)event.getDamager();
            if (sm.store.isJoinedPlayer(attacker.getUniqueId()) && attacker.getUniqueId().equals(this.sm.store.getTaggedPlayer())) {
                // Attacker is it
                if (event.getEntity() instanceof Player) {
                    Player defender = (Player)event.getEntity();
                    if (sm.store.isJoinedPlayer(defender.getUniqueId())) {
                        this.sm.tagWith(defender, attacker, attacker.getInventory().getItemInMainHand());
                    }
                } else if (event.getEntity() instanceof ArmorStand) {
                    this.sm.tagWith(null, attacker, attacker.getInventory().getItemInMainHand());
                }
            }
        }
    }
}
