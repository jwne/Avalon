package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "setrally", aliases = {}, permission = "", description = "Update your team's rally point.")
public class SetRally extends TeamCommand {

    @Override
    public boolean managerCommand() {
        return true;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
        if(team == null) {
            player.sendMessage("§cYou are not on a team!");
            return;
        }

        team.setRally(player.getLocation());
        player.sendMessage("§9Team rally point set.");
        team.message("§9" + player.getName() + " has updated the team rally point!");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
