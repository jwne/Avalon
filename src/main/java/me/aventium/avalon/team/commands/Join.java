package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "join", aliases = {"j"}, permission = "", description = "Join a team")
public class Join extends TeamCommand {

    @Override
    public boolean managerCommand() {
        return false;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/team join <name> [password]");
            return;
        }

        String name = args[0];

        Team team = Avalon.get().getTeamManager().getTeam(name);

        if(Avalon.get().getTeamManager().getTeam(args[0]) == null) {
            player.sendMessage("§cA team with that name does not exist!");
            return;
        }

        if(team.getPassword() != "none" && (!args[1].equals(team.getPassword()) || args.length < 2)) {
            if(args.length < 2) {
                player.sendMessage("§c/team join <name> [password]");
                return;
            }

            player.sendMessage("§cIncorrect password!");
            return;
        }

        team.addMember(player.getUniqueId(), player.getName());
        Avalon.get().getTeamManager().setTeam(player, team);
        player.sendMessage("§9Welcome to the team.");
        team.message("§9" + player.getName() + " has joined the team.");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }

}
