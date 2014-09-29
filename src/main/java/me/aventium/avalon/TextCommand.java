package me.aventium.avalon;

import me.aventium.avalon.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TextCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if(!(player.isOp())) return true;

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            sb.append(args[i] + " ");
        }

        String text = sb.toString().trim();

        TextUtils.MakeText(text, player.getLocation(), BlockFace.NORTH, Material.DIAMOND_BLOCK.getId(), (byte) 0, TextUtils.TextAlign.CENTER, true);
        return true;
    }
}
