package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "sethq", aliases = {}, permission = "team.command.sethq", description = "Update your team's HQ.")
public class SetHQ extends TeamCommand {

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

        team.setHQ(player.getLocation());
        player.sendMessage("§9Team HQ set.");
        team.message("§9" + player.getName() + " has updated the team HQ!");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
