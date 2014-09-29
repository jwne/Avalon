package me.aventium.avalon.koth.command.commands;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import me.aventium.avalon.koth.CapturePoint;
import me.aventium.avalon.koth.command.KOTHCommand;
import org.bukkit.entity.Player;

@Command(name = "capturepoints", aliases = {"cappoints", "cpoints", "cp"}, permission = "koth.getpoints", description = "List all capture points and whether they are active or not")
public class CapturePoints extends KOTHCommand {

    @Override
    public void syncExecute(Player player, String[] args) {
        player.sendMessage("§7----[§6KOTH Capture Points§7]----");
        for(CapturePoint cp : Avalon.get().getCapturePointModule().getCapturePoints()) {
            player.sendMessage("§7- §6" + cp.getName() + " §7- §6" + (cp.isActive() ? "Active" : "Inactive"));
        }
    }

    @Override
    public void asyncExecute(Player player, String[] args) {}
}
