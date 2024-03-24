package com.chellrose.minechell;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chellrose.minechell.hat.CommandHat;
import com.chellrose.minechell.head.CommandHead;
import com.chellrose.minechell.head.ListenerPlayerChargedCreeperDeath;
import com.chellrose.minechell.head.ListenerWanderingTrader;
import com.chellrose.minechell.itemframe.ListenerInvisibleItemFrame;
import com.chellrose.minechell.sit.ListenerPlayerSitDown;
import com.chellrose.minechell.tag.StateMachineTag;

public class App extends JavaPlugin {
    public static Logger logger;

    private StateMachineTag tag;
    private ListenerPlayerSitDown sit;

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
        this.sit = new ListenerPlayerSitDown(this);
        pluginManager.registerEvents(this.sit, this);

        // ItemFrame
        pluginManager.registerEvents(new ListenerInvisibleItemFrame(), this);

        getLogger().info("MineChell enabled.");
    }
    
    @Override
    public void onDisable() {
        this.tag.handleDisable();
        this.sit.handleDisable();
        getLogger().info("MineChell disabled.");
    }
}
