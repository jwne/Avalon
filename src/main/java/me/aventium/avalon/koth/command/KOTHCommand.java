package me.aventium.avalon.koth.command;

import org.bukkit.entity.Player;

public abstract class KOTHCommand {

    public abstract void syncExecute(Player player, String[] args);

    public abstract void asyncExecute(Player player, String[] args);

}
