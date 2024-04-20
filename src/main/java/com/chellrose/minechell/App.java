package com.chellrose.minechell;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chellrose.minechell.armorstand.ListenerArmorStand;
import com.chellrose.minechell.carry.ListenerPlayerCarry;
import com.chellrose.minechell.hat.CommandHat;
import com.chellrose.minechell.head.CommandHead;
import com.chellrose.minechell.head.ListenerPlayerChargedCreeperDeath;
import com.chellrose.minechell.head.ListenerWanderingTrader;
import com.chellrose.minechell.invis.ListenerMakeInvis;
import com.chellrose.minechell.invis.ListenerRemoveItemFromInvis;
import com.chellrose.minechell.invis.ListenerWashInvis;
import com.chellrose.minechell.minechell.CommandMineChell;
import com.chellrose.minechell.sit.ListenerPlayerSitDown;
import com.chellrose.minechell.tag.StateMachineTag;
import com.chellrose.minechell.wrench.ItemWrench;
import com.chellrose.minechell.wrench.ListenerItemWrench;
import com.chellrose.minechell.wrench.ListenerWrenchArmorStand;
import com.chellrose.minechell.wrench.ListenerWrenchBlock;

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

        // Invis item frames/armor stands
        pluginManager.registerEvents(new ListenerMakeInvis(), this);
        pluginManager.registerEvents(new ListenerWashInvis(), this);
        pluginManager.registerEvents(new ListenerRemoveItemFromInvis(), this);

        // Armor stands
        pluginManager.registerEvents(new ListenerArmorStand(), this);

        // Wrench
        new ItemWrench(this);
        pluginManager.registerEvents(new ListenerItemWrench(), this);
        pluginManager.registerEvents(new ListenerWrenchArmorStand(), this);
        pluginManager.registerEvents(new ListenerWrenchBlock(), this);

        // MineChell command
        this.getCommand(CommandMineChell.COMMAND).setExecutor(new CommandMineChell());

        // Carry
        pluginManager.registerEvents(new ListenerPlayerCarry(), this);

        getLogger().info("MineChell enabled.");
    }
    
    @Override
    public void onDisable() {
        this.tag.handleDisable();
        this.sit.handleDisable();
        getLogger().info("MineChell disabled.");
    }
}
