package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "password", aliases = {"pass"}, permission = "", description = "Update your team's password")
public class Password extends TeamCommand {

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

        if(args.length < 1) {
            player.sendMessage("§c/team pasword <password>");
            return;
        }

        String pass = args[0];

        team.setPassword(pass);
        player.sendMessage("§7Password changed to '" + pass + "'");
        team.message("§9" + player.getName() + " has changed the password to '" + pass + "'");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}