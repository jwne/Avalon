package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.entity.Player;

@Command(name = "friendlyfire", aliases = {"ff"}, permission = "", description = "Toggle friendly fire")
public class FriendlyFire extends TeamCommand {


    @Override
    public boolean managerCommand() {
        return true;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/team friendlyfire <on|off>");
            return;
        }

        if(!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
            player.sendMessage("§c/team friendlyfire <on|off>");
            return;
        }

        boolean toggle = args[0].equalsIgnoreCase("on");

        Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
        if(team == null) {
            player.sendMessage("§cYou are not on a team!");
            return;
        }

        team.setFriendlyFire(toggle);
        team.message("§9" + player.getName() + " has turned " + (toggle ? "on" : "off") + " friendly fire!");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
