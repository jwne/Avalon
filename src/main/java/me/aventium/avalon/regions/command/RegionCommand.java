package me.aventium.avalon.regions.command;

import org.bukkit.entity.Player;

public abstract class RegionCommand {

    public abstract void syncExecute(Player player, String[] args);

    public abstract void asyncExecute(Player player, String[] args);

}
