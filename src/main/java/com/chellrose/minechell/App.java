package com.chellrose.minechell;

import org.bukkit.plugin.java.JavaPlugin;

import com.chellrose.minechell.hat.CommandHat;
import com.chellrose.minechell.head.CommandHead;
import com.chellrose.minechell.head.ListenerPlayerChargedCreeperDeath;
import com.chellrose.minechell.head.ListenerWanderingTrader;
import com.chellrose.minechell.tag.StateMachineTag;

// TODO: Tag: require shown on dynmap
// TODO: Tag scoreboard: who's currently it, how long they've been it
// TODO: markdown syntax in chat messages
// TODO: sticks/shears to attach/remove arms on armorstand
// TODO: Trident retrieve items
// TODO: saddle on head = ride people

public class App extends JavaPlugin {
    private StateMachineTag tag;

    @Override
    public void onEnable() {
        this.getCommand(CommandHat.COMMAND).setExecutor(new CommandHat());

        this.getCommand(CommandHead.COMMAND).setExecutor(new CommandHead());
        this.getServer().getPluginManager().registerEvents(new ListenerWanderingTrader(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerPlayerChargedCreeperDeath(), this);

        this.tag = new StateMachineTag(this);

        getLogger().info("MineChell enabled.");
    }
    
    @Override
    public void onDisable() {
        tag.handleDisable();
        getLogger().info("MineChell disabled.");
    }
}
