package me.aventium.avalon.regions.command.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.regions.command.RegionCommand;
import org.bukkit.entity.Player;

@me.aventium.avalon.Command(name = "delete", aliases = {"d"}, permission = "regions.delete", description = "Delete a region")
public class Delete extends RegionCommand {

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/region delete <name>");
            return;
        }

        String name = args[0];

        if(!Avalon.get().getRegionModule().regionExists(name)) {
            player.sendMessage("§cA region with that name does not exist!");
            return;
        }

        Avalon.get().getRegionModule().deleteRegion(name);
        player.sendMessage("§cRegion deleted!");
    }

    @Override
    public void asyncExecute(Player player, String[] args) {

    }
}
