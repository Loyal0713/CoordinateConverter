package com.loyal0713.coordinateconverter;

import org.bukkit.plugin.java.JavaPlugin;

public class CoordinateConverter extends JavaPlugin {

    @Override
    public void onEnable() {
        CoordConvCommand handler = new CoordConvCommand();
        getCommand("coordconv").setExecutor(handler);
        getCommand("coordconv").setTabCompleter(handler);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info(getName() + " disabled.");
    }
}
