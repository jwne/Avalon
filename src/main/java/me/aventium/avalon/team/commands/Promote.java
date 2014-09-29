package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(name = "promote", aliases = {"p"}, permission = "", description = "Promote a player to manager")
public class Promote extends TeamCommand {


    @Override
    public boolean managerCommand() {
        return true;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/team promote <player>");
            return;
        }

        Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
        if(team == null) {
            player.sendMessage("§cYou are not on a team!");
            return;
        }

        if(!team.hasPlayer(args[0])) {
            player.sendMessage("§cThere is no player by that name on the team!");
            return;
        }

        if(team.isManager(args[0])) {
            player.sendMessage("§cThat player is already a manager!");
            return;
        }

        UUID uuid = null;

        for(UUID id : team.getMembers().keySet()) {
            if(team.getMembers().get(id).equalsIgnoreCase(args[0])) {
                uuid = id;
                break;
            }
        }

        if(uuid == null) {
            player.sendMessage("§cThere is no player by that name on the team!");
            return;
        }

        String name = team.getMembers().get(uuid);

        team.addManager(uuid, name);
        team.removeMember(name);
        team.message("§9" + name + " has been promoted by " + player.getName() + ".");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
