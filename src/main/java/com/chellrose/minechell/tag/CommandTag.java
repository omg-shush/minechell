package com.chellrose.minechell.tag;

import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chellrose.minechell.Util;
import com.chellrose.minechell.head.CommandHead;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandTag implements CommandExecutor {
    public static final String COMMAND = "tag";
    public static final String COMMAND_JOIN = "join";
    public static final String COMMAND_LEAVE = "leave";

    private StateMachineTag sm;

    public CommandTag(StateMachineTag sm) {
        this.sm = sm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && args[0].equals("set")) {
            if (sender.isPermissionSet("minechell.tag.set")) {
                Player newPlayer = sender.getServer().getPlayer(args[1]);
                UUID newUuid = newPlayer == null ? null : newPlayer.getUniqueId();
                newUuid = newUuid == null ? CommandHead.getUUIDFromPlayerName(args[1]) : newUuid;
                OfflinePlayer tagged = newUuid == null ? null : sender.getServer().getOfflinePlayer(newUuid);
                if (tagged == null) {
                    sender.sendMessage("Failed to find player " + args[1]);
                } else if (tagged.getUniqueId().equals(this.sm.store.getTaggedPlayer())) {
                    sender.sendMessage("Player " + args[1] + " is already it.");
                } else if (!this.sm.store.isJoinedPlayer(tagged.getUniqueId())) {
                    sender.sendMessage("Player " + args[1] + " isn't playing tag.");
                } else {
                    sender.sendMessage("Tagged " + args[1] + "!");
                    this.sm.tag(tagged);
                }
            }
        } else if (sender instanceof Player) {
            Player player = (Player)sender;
            UUID uuid = player.getUniqueId();
            if (args.length == 0) {
                UUID tagged = this.sm.store.getTaggedPlayer();
                if (tagged == null) {
                    Util.sendItalic(player, "Nobody is currently it.");
                } else {
                    BaseComponent taggedName = new TextComponent(sender.getServer().getOfflinePlayer(tagged).getName());
                    taggedName.setColor(ChatColor.YELLOW);
                    BaseComponent message = new TextComponent(taggedName);
                    message.addExtra(" is currently it!");
                    message.setItalic(true);
                    player.spigot().sendMessage(message);
                }
            } else if (args.length > 1) {
                return false;
            } else {
                switch (args[0]) {
                case "list":
                case "online":
                    BaseComponent message = new TextComponent(args[0].equals("list") ? "Current tag players: " : "Online tag players: ");
                    Set<UUID> uuids = this.sm.store.joinedPlayers();
                    boolean first = true;
                    for (UUID u : uuids) {
                        BaseComponent playerName;
                        Player p = player.getServer().getPlayer(u);
                        if (p != null) {
                            // Online player
                            playerName = new TextComponent(p.getName());
                            playerName.setColor(ChatColor.YELLOW);
                        } else if (args[0].equals("list")) {
                            // Offline player included
                            OfflinePlayer o = player.getServer().getOfflinePlayer(u);
                            if (o != null) {
                                playerName = new TextComponent(o.getName());
                                playerName.setColor(ChatColor.GRAY);
                            } else {
                                // Player deleted, skip
                                continue;
                            }
                        } else {
                            // Offline player excluded
                            continue;
                        }
                        if (first) {
                            first = false;
                        } else {
                            message.addExtra(", ");
                        }
                        message.addExtra(playerName);
                    }
                    if (first) {
                        BaseComponent nobody = new TextComponent("Nobody ☹");
                        nobody.setColor(ChatColor.AQUA);
                        message.addExtra(nobody);
                    }
                    message.setItalic(true);
                    player.spigot().sendMessage(message);
                    break;
                case "join":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        Util.sendItalic(player, "You are already playing tag!");
                    } else {
                        this.sm.store.addJoinedPlayer(player);
                    }
                    break;
                case "leave":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        this.sm.store.removeJoinedPlayer(player);
                    } else {
                        Util.sendItalic(player, "You are not playing tag. Use /tag join");
                    }
                    break;
                case "vote":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        this.sm.handleVote(player, true);
                    } else {
                        Util.sendItalic(player, "You are not playing tag. Use /tag join");
                    }
                    break;
                case "unvote":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        this.sm.handleVote(player, false);
                    } else {
                        Util.sendItalic(player, "You are not playing tag. Use /tag join");
                    }
                    break;
                default:
                    return false;
                }
            }
        } else {
            sender.sendMessage("Only players can use that command.");
        }
        return true;
    }
}
