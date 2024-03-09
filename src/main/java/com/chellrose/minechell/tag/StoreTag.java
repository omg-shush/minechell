package com.chellrose.minechell.tag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class StoreTag {
    private static final String DATABASE_NAME = "tag";
    private static final String TABLE_NAME = "tag_players";
    private static final String TABLE_SCHEMA = "(player varchar(36), joined int, tagged int)";

    private Plugin plugin;
    private Connection db;
    private Set<UUID> joinedPlayers;
    private UUID taggedPlayer;

    private StoreTag(Plugin plugin) throws SQLException {
        this.plugin = plugin;
        this.joinedPlayers = new HashSet<>();
        this.taggedPlayer = null;
        plugin.getDataFolder().mkdirs();
        this.db = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/" + DATABASE_NAME + ".sqlite");
        Statement stmt = this.db.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " + TABLE_SCHEMA + ";");
        stmt.close();
    }

    public void close() throws SQLException {
        this.db.close();
    }

    public static StoreTag load(Plugin plugin) throws SQLException {
        StoreTag store = new StoreTag(plugin);
        Statement stmt = store.db.createStatement();

        ResultSet joined = stmt.executeQuery("SELECT player, tagged FROM " + TABLE_NAME + " WHERE joined = 1;");
        while (joined.next()) {
            UUID player = UUID.fromString(joined.getString("player"));
            boolean tagged = joined.getInt("tagged") == 1;
            store.joinedPlayers.add(player);
            if (tagged) {
                if (store.taggedPlayer != null) {
                    plugin.getLogger().warning("StoreTag: Error loading " + DATABASE_NAME + "." + TABLE_NAME + ": multiple tagged players");
                }
                store.taggedPlayer = player;
            }
        }
        joined.close();
        stmt.close();
        return store;
    }

    public void save() throws SQLException {
        Statement stmt = this.db.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "_new " + TABLE_SCHEMA + ";");

        if (!this.joinedPlayers.isEmpty()) {
            StringBuilder insert = new StringBuilder("INSERT INTO " + TABLE_NAME + "_new (player, joined, tagged) VALUES ");
            for (UUID player : this.joinedPlayers) {
                String tagged = player.equals(this.taggedPlayer) ? "1" : "0";
                insert.append("(\"" + player.toString() + "\", 1, " + tagged + "), ");
            }
            String insertAll = insert.toString();
            insertAll = insertAll.substring(0, insertAll.length() - 2) + ";";
            this.plugin.getLogger().info(insertAll); //TODO
            stmt.executeUpdate(insertAll);
        }

        // Swap to new table
        stmt.execute("DROP TABLE " + TABLE_NAME + ";");
        stmt.execute("ALTER TABLE " + TABLE_NAME + "_new RENAME TO " + TABLE_NAME + ";");

        stmt.close();
    }

    public void broadcastToJoinedPlayers(String message) {
        for (UUID uuid : this.joinedPlayers) {
            Player p = this.plugin.getServer().getPlayer(uuid);
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }

    public void broadcastToJoinedPlayers(BaseComponent message) {
        for (UUID uuid : this.joinedPlayers) {
            Player p = this.plugin.getServer().getPlayer(uuid);
            if (p != null) {
                p.spigot().sendMessage(message);
            }
        }
    }

    public void setTaggedPlayer(OfflinePlayer player) {
        this.taggedPlayer = player.getUniqueId();
    }

    public UUID getTaggedPlayer() {
        return this.taggedPlayer;
    }

    public void addJoinedPlayer(OfflinePlayer player) {
        this.joinedPlayers.add(player.getUniqueId());

        BaseComponent playerName = new TextComponent(player.getName());
        playerName.setColor(ChatColor.YELLOW);
        BaseComponent message = new TextComponent();
        message.addExtra(playerName);
        message.addExtra(" is now playing tag!");
        message.setItalic(true);
        this.broadcastToJoinedPlayers(message);
    }

    public void removeJoinedPlayer(OfflinePlayer player) {
        BaseComponent playerName = new TextComponent(player.getName());
        playerName.setColor(ChatColor.YELLOW);
        BaseComponent message = new TextComponent();
        message.addExtra(playerName);
        message.addExtra(" is no longer playing tag.");
        message.setItalic(true);
        this.broadcastToJoinedPlayers(message);

        this.joinedPlayers.remove(player.getUniqueId());
        if (player.getUniqueId().equals(this.taggedPlayer)) {
            this.taggedPlayer = null;
        }
    }

    public boolean isJoinedPlayer(UUID player) {
        return this.joinedPlayers.contains(player);
    }
}
