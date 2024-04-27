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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.chellrose.minechell.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandHead implements CommandExecutor {
    public static final String COMMAND = "head";

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
                        headMeta = this.apply(player, args[0], headMeta);
                        if (headMeta != null) {
                            handStack.setItemMeta(headMeta);
                            inv.setItemInMainHand(handStack);
                        }
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

    private SkullMeta apply(Player player, String arg, SkullMeta head) {
        try {
            try {
                UUID uuid = UUID.fromString(arg);
                applyUUID(uuid, head);
            } catch (IllegalArgumentException e) {
                try {
                    new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8);
                    applyBase64(arg, head);
                } catch (IllegalArgumentException e2) {
                    applyOwner(arg, head);
                }
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid name, uuid, or value");
            return null;
        }
        return head;
    }

    public static void applyUUID(UUID uuid, SkullMeta head) {
        PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
        head.setOwnerProfile(profile);
    }

    private void applyBase64(String base64, SkullMeta head) {
        // Decode base64
        String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        String url = JsonParser.parseString(json).getAsJsonObject()
            .get("textures").getAsJsonObject()
            .get("SKIN").getAsJsonObject()
            .get("url").getAsString();
        try {
            URL urlObj = URI.create(url).toURL();
            PlayerProfile profile = Bukkit.createPlayerProfile(base64);
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(urlObj);
            profile.setTextures(textures);
            head.setOwnerProfile(profile);
        } catch (MalformedURLException e) {
            return; // TODO validate outside
        }
    }

    private void applyOwner(String owner, SkullMeta head) {
        PlayerProfile profile = Bukkit.createPlayerProfile(owner);
        head.setOwnerProfile(profile);
    }

    public static UUID getUUIDFromPlayerName(String player) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + player)).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return UUID.fromString(json.get("id").getAsString());
        } catch (Exception e) {
            return null;
        }
    }
}
