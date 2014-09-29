package me.aventium.avalon.team.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.team.TeamCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(name = "info", aliases = {"i"}, permission = "", description = "View information about a team")
public class Info extends TeamCommand {

    @Override
    public boolean managerCommand() {
        return false;
    }

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length == 0) {
            Team team = Avalon.get().getTeamManager().getPlayerTeam(player);
            if(team == null) {
                player.sendMessage("§cYou are not on a team!");
                return;
            }
            sendInfo(player, team);
        } else {
            String playerName = args[0];
            UUID targetUUID = null;
            if(Bukkit.getPlayer(playerName) != null) {
                targetUUID = Bukkit.getPlayer(playerName).getUniqueId();
            } else {
                if(Avalon.get().getPlayerManager().getPlayerUUID(playerName) == null) {
                    player.sendMessage("§cPlayer not found!");
                    return;
                }

                targetUUID = Avalon.get().getPlayerManager().getPlayerUUID(playerName);
            }

            if(Avalon.get().getTeamManager().getPlayerTeam(targetUUID) == null) {
                player.sendMessage("§7That player is not on a team!");
                return;
            }
            sendInfo(player, Avalon.get().getTeamManager().getPlayerTeam(targetUUID));
        }
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }

    private void sendInfo(Player player, Team team) {
        player.sendMessage("§7----[ §9" + team.getName() + " §7]----");
        if(Avalon.get().getTeamManager().getPlayerTeam(player) == team) {
            player.sendMessage("§9Password: §7" + team.getPassword());
            player.sendMessage("§9Friendly Fire: §7" + (team.friendlyFireOn() ? "On" : "Off"));
        }
        player.sendMessage("§9HQ: §7" + (team.getHQ() == null ? "Not set" : "Set"));
        player.sendMessage("§9Rally: §7" + (team.getRally() == null ? "Not set" : "Set"));
        player.sendMessage("§9Members:");
        if(team.getManagers() != null && team.getManagers().size() != 0) {
            for(UUID uuid : team.getManagers().keySet()) {
                player.sendMessage("§9" + team.getManagers().get(uuid) + (Bukkit.getPlayer(uuid) != null ? " §7- " + Bukkit.getPlayer(uuid).getHealth() / 20 * 100 + "%" : " §7- Offline"));
            }
        }
        if(team.getMembers() != null && team.getMembers().size() != 0) {
            for(UUID uuid : team.getMembers().keySet()) {
                player.sendMessage("§7" + team.getMembers().get(uuid) + (Bukkit.getPlayer(uuid) != null ? " - " + Bukkit.getPlayer(uuid).getHealth() / 20 * 100 + "%" : " - Offline"));
            }
        }
    }
}
