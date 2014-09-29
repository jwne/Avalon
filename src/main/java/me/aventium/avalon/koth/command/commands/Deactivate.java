package me.aventium.avalon.koth.command.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.koth.CapturePoint;
import me.aventium.avalon.koth.command.KOTHCommand;
import org.bukkit.entity.Player;

@Command(name = "deactivate", aliases = {"deactive"}, permission = "koth.deactivate", description = "Deactivate a capture point so it cannot be captured.")
public class Deactivate extends KOTHCommand {

    @Override
    public void syncExecute(Player player, String[] args) {
        if(args.length < 1) {
            player.sendMessage("§c/koth deactivate <capturepoint>");
            return;
        }

        String name = args[0];

        if(!Avalon.get().getCapturePointModule().pointExists(name)) {
            player.sendMessage("§cThere is no capture point by that name created.");
            return;
        }

        CapturePoint cp = Avalon.get().getCapturePointModule().getCapturePoint(name);

        if(!cp.isActive()) {
            player.sendMessage("§cThat capture point is not active!");
            return;
        }

        cp.setActive(false);
        player.sendMessage("§6Capture point deactivated.");
        return;
    }

    @Override
    public void asyncExecute(Player player, String[] args) {}
}
