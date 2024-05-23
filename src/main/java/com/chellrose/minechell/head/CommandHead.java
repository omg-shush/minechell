package com.chellrose.minechell.head;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.chellrose.minechell.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandHead implements CommandExecutor {
    public static final String COMMAND = "head";

    private Plugin plugin;

    public CommandHead(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            PlayerInventory inv = player.getInventory();
            ItemStack handStack = inv.getItemInMainHand();
            
            if (handStack.getType() == Material.PLAYER_HEAD) {
                SkullMeta headMeta = (SkullMeta)handStack.getItemMeta();
                if (!headMeta.hasOwner()) {
                    if (args.length == 1) {
                        CompletableFuture<SkullMeta> future = apply(player, args[0], headMeta);
                        if (future == null) {
                            return false;
                        }
                        future.thenAcceptAsync(updatedHeadMeta -> {
                            if (player.isValid()) {
                                PlayerInventory newInv = player.getInventory();
                                ItemStack newHandStack = inv.getItemInMainHand();
                                if (newHandStack.getType() == Material.PLAYER_HEAD) {
                                    SkullMeta newHeadMeta = (SkullMeta)newHandStack.getItemMeta();
                                    if (!newHeadMeta.hasOwner()) {
                                        newHandStack.setItemMeta(updatedHeadMeta);
                                        newInv.setItemInMainHand(newHandStack);
                                    }
                                }
                            }
                        }, runnable -> Bukkit.getScheduler().runTask(this.plugin, runnable));
                    } else {
                        player.sendMessage("Usage: /head <player name, uuid, or value>");
                    }
                } else {
                    Util.sendItalic(player, "You must clean the player head first! Perhaps a travelling merchant might be able to help...");
                }
            } else {
                Util.sendItalic(player, "You can only use /head while holding a player head.");
            }
        } else {
            sender.sendMessage("Only players can use that command.");
        }
        return true;
    }

    private CompletableFuture<SkullMeta> apply(Player player, String arg, SkullMeta head) {
        try {
            try {
                if (arg.length() == 32) {
                    arg = addUUIDDashes(arg);
                }
                UUID uuid = UUID.fromString(arg);
                return this.applyUUID(uuid, head);
            } catch (IllegalArgumentException e) {
                try {
                    new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8);
                    return applyBase64(player, arg, head);
                } catch (Exception e2) {
                    return applyOwner(arg, head);
                }
            }
        } catch (Exception e) {
            player.sendMessage("Invalid name, uuid, or value (" + e.getLocalizedMessage() + ")");
            return null;
        }
    }

    public CompletableFuture<SkullMeta> applyUUID(UUID uuid, SkullMeta head) {
        PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
        return profile.update().thenApplyAsync(updatedProfile -> {
            head.setOwnerProfile(updatedProfile);
            return head;
        }, runnable -> Bukkit.getScheduler().runTask(this.plugin, runnable));
    }

    private CompletableFuture<SkullMeta> applyBase64(Player player, String base64, SkullMeta head) {
        // Decode base64
        String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        String url = JsonParser.parseString(json).getAsJsonObject()
            .get("textures").getAsJsonObject()
            .get("SKIN").getAsJsonObject()
            .get("url").getAsString();
        try {
            URL urlObj = URI.create(url).toURL();
            PlayerProfile profile = player.getPlayerProfile();
            return profile.update().thenApplyAsync(updatedProfile -> {
                PlayerTextures textures = updatedProfile.getTextures();
                textures.setSkin(urlObj);
                updatedProfile.setTextures(textures);
                head.setOwnerProfile(updatedProfile);
                return head;
            }, runnable -> Bukkit.getScheduler().runTask(this.plugin, runnable));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<SkullMeta> applyOwner(String owner, SkullMeta head) {
        PlayerProfile profile = Bukkit.getOfflinePlayer(getUUIDFromPlayerName(owner)).getPlayerProfile();
        return profile.update().thenApplyAsync(updatedProfile -> {
            head.setOwnerProfile(updatedProfile);
            return head;
        }, runnable -> Bukkit.getScheduler().runTask(this.plugin, runnable));
    }

    public static UUID getUUIDFromPlayerName(String player) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + player)).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return UUID.fromString(addUUIDDashes(json.get("id").getAsString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String addUUIDDashes(String idNoDashes) {
        StringBuffer idBuff = new StringBuffer(idNoDashes);
        idBuff.insert(20, '-');
        idBuff.insert(16, '-');
        idBuff.insert(12, '-');
        idBuff.insert(8, '-');
        return idBuff.toString();
    }
}
