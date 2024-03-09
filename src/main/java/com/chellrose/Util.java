package com.chellrose;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Util {
    public static void sendItalic(Player player, String message) {
        BaseComponent c = new TextComponent(message);
        c.setItalic(true);
        player.spigot().sendMessage(c);
    }
}
