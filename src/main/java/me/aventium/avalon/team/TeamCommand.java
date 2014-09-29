package me.aventium.avalon.team;

import org.bukkit.entity.Player;

public abstract class TeamCommand {

    public abstract boolean managerCommand();

    public abstract void syncExecute(Player player, String[] args);

    public abstract void asyncExecute(Player player, String[] args);

}
