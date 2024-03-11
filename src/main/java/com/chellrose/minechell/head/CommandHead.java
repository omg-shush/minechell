package com.chellrose.minechell.head;

import java.net.URI;
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

import com.chellrose.minechell.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;

public class CommandHead implements CommandExecutor {
    public static final String COMMAND = "head";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            PlayerInventory inv = player.getInventory();
            ItemStack handStack = inv.getItemInMainHand();
            
            if (handStack.getType() == Material.PLAYER_HEAD) {
                NBTItem handNbt = new NBTItem(handStack);
                if (!handNbt.hasNBTData()) {
                    if (args.length == 1) {
                        this.apply(player, args[0], handNbt);
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

    private void apply(Player player, String arg, NBTItem item) {
        try {
            try {
                UUID uuid = UUID.fromString(arg);
                applyUUID(uuid, item);
            } catch (IllegalArgumentException e) {
                try {
                    new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8);
                    applyBase64(arg, item);
                } catch (IllegalArgumentException e2) {
                    applyOwner(arg, item);
                }
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid name, uuid, or value");
            return;
        }
        player.getInventory().setItemInMainHand(item.getItem());
    }

    public static void applyUUID(UUID uuid, NBTItem item) {
        NBTCompound skullOwner = item.addCompound("SkullOwner");
        skullOwner.setUUID("Id", uuid);
        skullOwner.setString("Name", Bukkit.getOfflinePlayer(uuid).getName());
    }

    private void applyBase64(String base64, NBTItem item) {
        NBTCompound skullOwner = item.addCompound("SkullOwner");
        skullOwner.setUUID("Id", UUID.randomUUID());
        NBTListCompound texture = skullOwner.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", base64);
    }

    private void applyOwner(String owner, NBTItem item) {
        NBTCompound skullOwner = item.addCompound("SkullOwner");
        skullOwner.setString("Name", owner);
        skullOwner.setUUID("Id", getUUIDFromPlayerName(owner));
        item.setString("SkullOwner", owner);
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
