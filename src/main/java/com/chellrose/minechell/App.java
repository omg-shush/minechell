package com.chellrose.minechell;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chellrose.minechell.hat.CommandHat;
import com.chellrose.minechell.head.CommandHead;
import com.chellrose.minechell.head.ListenerPlayerChargedCreeperDeath;
import com.chellrose.minechell.head.ListenerWanderingTrader;
import com.chellrose.minechell.sit.ListenerPlayerSitDown;
import com.chellrose.minechell.tag.StateMachineTag;

// TODO: Sit!
// TODO: chat channels
// TODO: /yoink and /unyoink
// TODO: Tag: require shown on dynmap
// TODO: Tag scoreboard: who's currently it, how long they've been it
// TODO: markdown syntax in chat messages
// TODO: sticks/shears to attach/remove arms on armorstand
// TODO: Trident retrieve items
// TODO: saddle on head = ride people

public class App extends JavaPlugin {
    public static Logger logger;

    private StateMachineTag tag;

    @Override
    public void onEnable() {
        App.logger = this.getLogger();
        PluginManager pluginManager = this.getServer().getPluginManager();

        // Hat
        this.getCommand(CommandHat.COMMAND).setExecutor(new CommandHat());

        // Head
        this.getCommand(CommandHead.COMMAND).setExecutor(new CommandHead());
        pluginManager.registerEvents(new ListenerWanderingTrader(), this);
        pluginManager.registerEvents(new ListenerPlayerChargedCreeperDeath(), this);

        // Tag
        this.tag = new StateMachineTag(this);

        // Sit
        pluginManager.registerEvents(new ListenerPlayerSitDown(), this);

        getLogger().info("MineChell enabled.");
    }
    
    @Override
    public void onDisable() {
        tag.handleDisable();
        getLogger().info("MineChell disabled.");
    }
}
