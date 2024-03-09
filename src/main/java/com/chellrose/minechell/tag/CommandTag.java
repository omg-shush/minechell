package com.chellrose.minechell.tag;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chellrose.minechell.head.CommandHead;

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
                    player.sendMessage("Nobody is currently it.");
                } else {
                    player.sendMessage(sender.getServer().getOfflinePlayer(tagged).getName() + " is currently it!");
                }
            } else if (args.length > 1) {
                sender.sendMessage("Too many parameters!");
            } else {
                switch (args[0]) {
                case "join":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        sender.sendMessage("You are already playing tag!");
                    } else {
                        this.sm.store.addJoinedPlayer(player);
                    }
                    break;
                case "leave":
                    if (this.sm.store.isJoinedPlayer(uuid)) {
                        this.sm.store.removeJoinedPlayer(player);
                    } else {
                        sender.sendMessage("You are not playing tag.");
                    }
                    break;
                case "vote":
                    this.sm.handleVote(player, true);
                    break;
                case "unvote":
                    this.sm.handleVote(player, false);
                    break;
                default:
                    sender.sendMessage("Unrecognized subcommand " + args[0]);
                    break;
                }
            }
        } else {
            sender.sendMessage("Only players can use that command.");
        }
        return true;
    }
}
