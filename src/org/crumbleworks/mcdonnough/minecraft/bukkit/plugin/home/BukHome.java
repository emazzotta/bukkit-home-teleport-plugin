package org.crumbleworks.mcdonnough.minecraft.bukkit.plugin.home;

import org.bukkit.plugin.java.JavaPlugin;


public final class BukHome extends JavaPlugin {

    private BukHomeDatabase bukHomeDatabase;

    public BukHome() {
        bukHomeDatabase = BukHomeDatabase.getInstance(this);
    }

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");

        bukHomeDatabase.connect();
        if(!bukHomeDatabase.isDatabaseTableExisting()) {
            bukHomeDatabase.createBukHomeTable();
        }

        getCommand("home").setExecutor(new BukHomeCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
        bukHomeDatabase.close();
    }
}