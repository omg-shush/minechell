package com.chellrose.minechell.tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StateMachineTag {
    protected StoreTag store;

    private JavaPlugin plugin;
    private Set<UUID> votedPlayers;
    private Random rand;

    public StateMachineTag(JavaPlugin plugin) {
        this.plugin = plugin;
        this.votedPlayers = new HashSet<>();
        this.rand = new Random();

        this.plugin.getCommand(CommandTag.COMMAND).setExecutor(new CommandTag(this));
        this.plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                handlePlayerDisconnect(e.getPlayer());
            }
        }, this.plugin);
        try {
            this.store = StoreTag.load(plugin);
        } catch (SQLException e) {
            this.plugin.getLogger().warning("MineChell: Failed to load tag: " + e.getMessage());
        }
    }

    public void handleDisable() {
        try {
            this.store.save();
            this.store.close();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("MineChell: Failed to save tag: " + e.getMessage());
        }
    }

    public void handleVote(Player player, boolean vote) {
        if (vote) {
            this.votedPlayers.add(player.getUniqueId());
            player.sendMessage("You voted! " + this.votedPlayers.size() + " / " + this.requiredVote() + " votes needed.");
        } else {
            this.votedPlayers.remove(player.getUniqueId());
            player.sendMessage("You unvoted. "  + this.votedPlayers.size() + " / " + this.requiredVote() + " votes needed.");
        }
        checkVote();
    }

    public int requiredVote() {
        return Math.max(2, Math.min(3, this.plugin.getServer().getOnlinePlayers().size()));
    }

    public void checkVote() {
        if (this.votedPlayers.size() >= this.requiredVote()) {
            // Vote succeeded, select random player to tag
            List<Player> onlineJoinedPlayers = new ArrayList<>();
            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                if (this.store.isJoinedPlayer(p.getUniqueId())) {
                    onlineJoinedPlayers.add(p);
                }
            }
            Player tagged = onlineJoinedPlayers.get(this.rand.nextInt(onlineJoinedPlayers.size()));
            this.tag(tagged);
            // Reset vote
            votedPlayers.clear();
        }
    }

    public void tagWith(Player tagged, Player tagger, ItemStack with) {
        this.store.setTaggedPlayer(tagged);
        String item = "FIST";
        if (with != null) {
            if (with.getItemMeta().hasDisplayName()) {
                item = with.getItemMeta().getDisplayName();
            }
            if (with.getAmount() > 1) {
                item = with.getAmount() + " " + item + "s!";
            } else {
                item = "a " + item + "!";
            }
        }
        this.store.broadcastToJoinedPlayers("TAG! " + tagger.getName() + " tagged " + tagged.getName() + " with " + item);
    }

    public void tag(OfflinePlayer player) {
        this.store.setTaggedPlayer(player);
        if (player.isOnline()) {
            Player p = player.getPlayer();
            p.getWorld().strikeLightningEffect(p.getLocation());
        }
        this.store.broadcastToJoinedPlayers("A mysterious force reaches from above...");
        this.store.broadcastToJoinedPlayers("TAG! " + player.getName() + " is now it!");
    }

    public void handlePlayerDisconnect(Player player) {
        this.votedPlayers.remove(player.getUniqueId());
        checkVote();
    }
}
