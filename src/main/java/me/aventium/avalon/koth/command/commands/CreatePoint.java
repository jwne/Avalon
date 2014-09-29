package me.aventium.avalon.koth.command.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.koth.command.KOTHCommand;
import org.bukkit.entity.Player;

@Command(name = "createpoint", aliases = {"cpoint"}, permission = "koth.createpoint", description = "Create a new KOTH capture point, capture time must be in minutes.")
public class CreatePoint extends KOTHCommand {

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage("§c/koth createpoint <name> <captureTime> - Capture time has to be in minutes");
            return;
        }

        String name = args[0];
        int min = 0;
        try {
            min = Integer.parseInt(args[1]);
        } catch(NumberFormatException ex) {
            player.sendMessage("§cTime to capture must be in minutes, with numbers only!");
            return;
        }

        long duration = 1200L * min;

        if(Avalon.get().getCapturePointModule().pointExists(name)) {
            player.sendMessage("§cA point with that name already exists!");
            return;
        }

        if(duration <= 0) {
            player.sendMessage("§cInvalid duration for capture time!");
            return;
        }



        Avalon.get().getCapturePointModule().createNewPoint(name, player, duration);
    }

    @Override
    public void asyncExecute(Player player, String[] args) {}
}
