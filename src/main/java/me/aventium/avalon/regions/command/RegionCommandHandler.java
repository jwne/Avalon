package me.aventium.avalon.regions.command;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RegionCommandHandler implements CommandExecutor {

    private Map<Command, RegionCommand> commands = new HashMap<>();

    public RegionCommandHandler(Plugin plugin) {
        for(Class<?> clazz : Avalon.get().getClassesInPackage("me.aventium.avalon.regions.command.commands")) {
            try {
                if(clazz.getAnnotation(Command.class) != null) {
                    Command c = clazz.getAnnotation(Command.class);
                    RegionCommand command = (RegionCommand) clazz.newInstance();
                    commands.put(c, command);
                }
            } catch(InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cYou cannot use this command in console!");
            return true;
        }

        final Player player = (Player) sender;
        if(args.length == 0) {
            player.sendMessage("§7----[ §cRegion Commands §7]----");
            for(Command c : commands.keySet()) {
                if(player.hasPermission(c.permission())) {
                    player.sendMessage("§7/" + label + " §6" + c.name() + " §7- " + c.description());
                }
            }
            return true;
        }

        String subCommand = args[0];
        Vector<String> newArgs = new Vector<>(Arrays.asList(args));
        newArgs.remove(0);
        final String[] arguments = newArgs.toArray(new String[0]);

        final Command cmd = getSubCommand(subCommand);
        if(cmd == null) {
            player.sendMessage("§7Unknown Region command! Use /§c" + label + " §7for help on managing regions!");
            return true;
        }

        if(!player.hasPermission(cmd.permission())) {
            player.sendMessage("§cYou do not have permission to use this command!");
            return true;
        }

        commands.get(cmd).syncExecute(player, arguments);

        Bukkit.getScheduler().runTaskAsynchronously(Avalon.get(), new Runnable() {
            @Override
            public void run() {
                commands.get(cmd).asyncExecute(player, arguments);
            }
        });
        return true;
    }

    private Command getSubCommand(String sub) {
        for(Command command : commands.keySet()) {
            if(command.name().equalsIgnoreCase(sub)) return command;
            for(String alias : command.aliases()) {
                if(alias.equalsIgnoreCase(sub)) return command;
            }
        }
        return null;
    }
}
