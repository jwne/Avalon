package me.aventium.avalon.utils;

import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Chat {

    public static String PREFIX = "§7[§cKOTH§7]";
    public static ChatColor IMPORTANT_COLOR = ChatColor.RED;
    public static ChatColor BASE_COLOR = ChatColor.GRAY;

    public static void broadcast(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(PREFIX + " " + IMPORTANT_COLOR + message);
        }
    }

}
