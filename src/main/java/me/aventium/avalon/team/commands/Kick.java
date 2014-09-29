package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "kick", aliases = {}, permission = "", description = "Kick a player from your team")
public class Kick extends TeamCommand {

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

        if(!team.hasPlayer(args[0])) {
            player.sendMessage("§cThere is no player by that name on the team!");
            return;
        }

        team.removePlayer(Avalon.get().getPlayerManager().getPlayerUUID(args[0]));
        Avalon.get().getTeamManager().setTeam(Avalon.get().getPlayerManager().getPlayerUUID(args[0]), null);
        team.message("§9" + args[0] + " has been kicked by " + player.getName());
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }

}
