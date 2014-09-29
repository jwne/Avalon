package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "create", aliases = {}, permission = "", description = "Create a team")
public class Create extends TeamCommand {

    @Override
    public boolean managerCommand() {
        return false;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/team create <name>");
            return;
        }
        Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
        if(team != null) {
            player.sendMessage("§cYou are already in a team!");
            return;
        }

        if(Avalon.get().getTeamManager().getTeam(args[0]) != null) {
            player.sendMessage("§cA team with that name already exists!");
            return;
        }

        Team newTeam = new Team(args[0], player.getUniqueId(), player.getName());
        Avalon.get().getTeamManager().addTeam(newTeam);
        Avalon.get().getTeamManager().setTeam(player, newTeam);
        player.sendMessage("§9Team created successfully!");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }

}
