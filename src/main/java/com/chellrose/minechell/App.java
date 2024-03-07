package com.chellrose.minechell;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand(CommandHat.COMMAND).setExecutor(new CommandHat());
        this.getCommand(CommandHead.COMMAND).setExecutor(new CommandHead());
        this.getServer().getPluginManager().registerEvents(new ListenerWanderingTrader(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerPlayerChargedCreeperDeath(), this);
        getLogger().info("MineChell enabled.");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MineChell disabled.");
    }
}
