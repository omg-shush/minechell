package com.chellrose.minechell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandHat implements CommandExecutor {
    public static final String COMMAND = "hat";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            PlayerInventory inv = player.getInventory();
            ItemStack helmetStack = inv.getHelmet();
            ItemStack handStack = inv.getItemInMainHand();
            ItemStack newHelmetStack = handStack;
            ItemStack newHandStack = helmetStack;
            inv.setHelmet(newHelmetStack);
            inv.setItemInMainHand(newHandStack);
            player.sendMessage("It's on your head now!");
        } else {
            sender.sendMessage("Only players can use that command.");
        }
        return true;
    }
}
