package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "leave", aliases = {"l"}, permission = "", description = "Leave your current team.")
public class Leave extends TeamCommand {

    @Override
    public boolean managerCommand() {
        return false;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
        if(team == null) {
            player.sendMessage("§cYou are not on a team!");
            return;
        }

        boolean disband = false;

        if(team.getMembers().size() + team.getManagers().size() == 1) {
            disband = true;
        }

        if(disband) {
            team.remove = true;
            team.save();
            player.sendMessage("§9Left and disbanded team.");
        } else {
            team.removePlayer(player.getUniqueId());
            team.message("§9" + player.getName() + " has left the team.");
        }

    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }

}
