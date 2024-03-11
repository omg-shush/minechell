package com.chellrose.minechell.tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.chellrose.minechell.Util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Item;

public class StateMachineTag {
    protected StoreTag store;
    protected JavaPlugin plugin;

    private Set<UUID> votedPlayers;
    private UUID lastTagged;
    private long lastTaggedTime;
    private Random rand;

    public StateMachineTag(JavaPlugin plugin) {
        this.plugin = plugin;
        this.votedPlayers = new HashSet<>();
        this.lastTagged = null;
        this.lastTaggedTime = -1;
        this.rand = new Random();

        this.plugin.getCommand(CommandTag.COMMAND).setExecutor(new CommandTag(this));
        this.plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                handlePlayerDisconnect(e.getPlayer());
            }
        }, this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new ListenerPlayerTag(this), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new ListenerProjectileTag(this), this.plugin);
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
            Util.sendItalic(player, "You voted! " + this.votedPlayers.size() + " / " + this.requiredVote() + " votes needed.");
        } else {
            this.votedPlayers.remove(player.getUniqueId());
            Util.sendItalic(player, "You unvoted. "  + this.votedPlayers.size() + " / " + this.requiredVote() + " votes needed.");
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

    public boolean isTagBack(Player tagged) {
        return tagged != null && tagged.getUniqueId().equals(this.lastTagged) && System.currentTimeMillis() - this.lastTaggedTime < 10_000;
    }

    public void tagWith(Player tagged, Player tagger, ItemStack with) {
        if (isTagBack(tagged)) {
            Player target = tagged == null ? tagger : tagged;
            target.getWorld().playSound(target, Sound.ENTITY_VILLAGER_HURT, 0.4f, 1.25f);
            Util.sendItalic(tagger, "No tagbacks!");
        } else {
            BaseComponent itemName = new TextComponent("a FIST!");
            if (with != null && with.getType() != Material.AIR) {
                if (with.hasItemMeta() && with.getItemMeta().hasDisplayName()) {
                    itemName = new TextComponent("[" + with.getItemMeta().getDisplayName() + "]");
                } else {
                    itemName = new TextComponent("[");
                    itemName.addExtra(new TranslatableComponent(with.getTranslationKey()));
                    itemName.addExtra(new TextComponent("]"));
                }
                ItemTag itemTag = ItemTag.ofNbt(with.getItemMeta() == null ? null : with.getItemMeta().getAsString());
                itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(with.getType().getKey().toString(), with.getAmount(), itemTag)));
                if (with.hasItemMeta()) {
                    itemName.setColor(ChatColor.AQUA);
                }
            }
            BaseComponent tail;
            if (with != null && with.getAmount() > 1) {
                tail = new TextComponent(with.getAmount() + "x ");
                tail.addExtra(itemName);
            } else {
                tail = new TextComponent(itemName);
            }

            BaseComponent message = new TextComponent("TAG! ");
            BaseComponent taggerName = new TextComponent(tagger.getName());
            taggerName.setColor(ChatColor.YELLOW);
            message.addExtra(taggerName);
            if (tagged == null) {
                // Dummy tag
                if (with == null) {
                    message.addExtra(" tagged an armor stand!");
                } else {
                    message.addExtra(" tagged an armor stand with ");
                    message.addExtra(tail);
                }
                message.setItalic(true);
                tagger.spigot().sendMessage(message);
            } else {
                this.lastTagged = this.store.getTaggedPlayer();
                this.lastTaggedTime = System.currentTimeMillis();
                this.store.setTaggedPlayer(tagged);

                message.addExtra(" tagged ");
                BaseComponent taggedName = new TextComponent(tagged.getName());
                taggedName.setColor(ChatColor.YELLOW);
                message.addExtra(taggedName);
                if (with == null) {
                    message.addExtra(new TextComponent("!"));
                } else {
                    message.addExtra(" with ");
                    message.addExtra(tail);
                }
                message.setItalic(true);
                this.store.broadcastToJoinedPlayers(message);
            }
        }
    }

    public void tag(OfflinePlayer player) {
        this.store.setTaggedPlayer(player);
        if (player.isOnline()) {
            Player p = player.getPlayer();
            p.getWorld().strikeLightningEffect(p.getLocation());
        }
        BaseComponent loreMsg = new TextComponent("A mysterious force reaches from above...");
        loreMsg.setItalic(true);
        this.store.broadcastToJoinedPlayers(loreMsg);
        BaseComponent playerName = new TextComponent(player.getName());
        playerName.setColor(ChatColor.YELLOW);
        BaseComponent message = new TextComponent("TAG! ");
        message.addExtra(playerName);
        message.addExtra(" is now it!");
        message.setItalic(true);
        this.store.broadcastToJoinedPlayers(message);
    }

    public void handlePlayerDisconnect(Player player) {
        this.votedPlayers.remove(player.getUniqueId());
        checkVote();
    }
}
