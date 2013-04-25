package org.crumbleworks.mcdonnough.minecraft.bukkit.plugin.home;

import org.bukkit.plugin.java.JavaPlugin;


public final class BukkitHomeTeleport extends JavaPlugin {

    private BukkitHomeTeleportDatabase bukkitHomeTeleportDatabase;

    public BukkitHomeTeleport() {
        bukkitHomeTeleportDatabase = BukkitHomeTeleportDatabase.getInstance(this);
    }

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");

        bukkitHomeTeleportDatabase.connect();
        if(!bukkitHomeTeleportDatabase.isDatabaseTableExisting()) {
            bukkitHomeTeleportDatabase.createBukHomeTable();
        }

        getCommand("home").setExecutor(new BukkitHomeTeleportCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
        bukkitHomeTeleportDatabase.close();
    }
}