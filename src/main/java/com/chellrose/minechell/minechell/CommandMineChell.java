package com.chellrose.minechell.minechell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CommandMineChell implements CommandExecutor {
    public static final String COMMAND = "minechell";
    public static final String URL = "https://github.com/omg-shush/minechell/blob/main/README.md";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            BaseComponent msg = new TextComponent("Click to open the MineChell documentation.");
            msg.setUnderlined(true);
            msg.setColor(ChatColor.BLUE);

            BaseComponent title = new TextComponent("MineChell\n");
            title.setColor(ChatColor.LIGHT_PURPLE);

            BaseComponent url = new TextComponent(URL);
            url.setUnderlined(true);
            url.setItalic(true);
            url.setBold(false);
            url.setColor(ChatColor.GRAY);

            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new BaseComponent[] { title }), new Text(new BaseComponent[] { url })));
            sender.spigot().sendMessage(msg);
            return true;
        } else if (args.length == 1 && args[0] == "version") {
            BaseComponent msg = new TextComponent("MineChell version " + getClass().getPackage().getImplementationVersion());
            msg.setColor(ChatColor.LIGHT_PURPLE);
            sender.spigot().sendMessage(msg);
            return true;
        } else {
            return false;
        }
    }
}
