package org.crumbleworks.mcdonnough.minecraft.bukkit.plugin.home;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class BukHomeDatabase {

    private static BukHomeDatabase bukHomeDatabaseInstance = new BukHomeDatabase();
    private static JavaPlugin javaPlugin;
    private static Database sqlite;

    private BukHomeDatabase() {}

    public static BukHomeDatabase getInstance(JavaPlugin javaPlugin) {
        BukHomeDatabase.javaPlugin = javaPlugin;
        return bukHomeDatabaseInstance;
    }

    public void close() {
        sqlite.close();
    }

    public void connect() {
        sqlite = new SQLite(Logger.getLogger("Minecraft"), "[BukHome]", javaPlugin.getDataFolder().getAbsolutePath(), "BukHome", ".sqlite");
        sqlite.open();
    }

    public ResultSet performQuery(String query) {
        try {
            return sqlite.query(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDatabaseTableExisting() {
        if(sqlite.isTable("buk_home")) {
            return true;
        }
        return false;
    }

    public void createBukHomeTable() {
        try {
            createTables();
            logCreationOfTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logCreationOfTables() {
        javaPlugin.getLogger().info("table 'home' has been created");
        javaPlugin.getLogger().info("table 'invitee' has been created");
        javaPlugin.getLogger().info("table 'home_invitee' has been created");
    }

    private void createTables() throws SQLException {
        createTableHome();
        createTableInvitee();
        createTableHomeInvitee();
    }

    private void createTableHomeInvitee() throws SQLException {
        sqlite.query("CREATE TABLE home_invitee(home_id INTEGER REFERENCES home(id), invitee_id INTEGER REFERENCES invitee(id), UNIQUE(home_id, invitee_id));");
    }

    private void createTableInvitee() throws SQLException {
        sqlite.query("CREATE TABLE invitee(id INTEGER PRIMARY KEY AUTOINCREMENT, playername VARCHAR(50));");
    }

    private void createTableHome() throws SQLException {
        sqlite.query("CREATE TABLE home(id INTEGER PRIMARY KEY AUTOINCREMENT, playername VARCHAR(50), homename VARCHAR(50), x REAL, y REAL, z REAL, UNIQUE(playername, homename));");
    }
}
