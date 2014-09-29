package me.aventium.avalon.regions.command.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.regions.command.RegionCommand;
import org.bukkit.entity.Player;

@me.aventium.avalon.Command(name = "create", aliases = {}, permission = "regions.create", description = "Create a new region")
public class CreateRegion extends RegionCommand {

    String[] deathbanOn = new String[] {"true", "yes"};
    String[] deathbanOff = new String[] {"false", "no"};

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage("§c/region create <name> <deathban>");
            return;
        }

        String name = args[0];

        boolean deathban = false;

        for(String str : deathbanOn) {
            if(args[1].equalsIgnoreCase(str)) {
                deathban = true;
                break;
            }
        }

        if(Avalon.get().getRegionModule().regionExists(name)) {
            player.sendMessage("§cA region with that name already exists!");
            return;
        }

        Avalon.get().getRegionModule().createNewPoint(name, player, deathban);
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
