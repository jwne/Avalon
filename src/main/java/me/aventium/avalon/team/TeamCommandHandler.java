package me.aventium.avalon.team;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TeamCommandHandler implements CommandExecutor {

    private Map<Command, TeamCommand> commands = new HashMap<>();

    public TeamCommandHandler(Plugin plugin) {
        for(Class<?> clazz : Avalon.get().getClassesInPackage("me.aventium.avalon.team.commands")) {
            try {
                if(clazz.getAnnotation(Command.class) != null && TeamCommand.class.isAssignableFrom(clazz)) {
                    Command c = clazz.getAnnotation(Command.class);
                    TeamCommand command = (TeamCommand) clazz.newInstance();
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
            player.sendMessage("§7----[ §9Player Commands §7]----");
            for(Command c : commands.keySet()) {
                if(!commands.get(c).managerCommand() && player.hasPermission(c.permission())) {
                    player.sendMessage("§7/" + label + " §9" + c.name() + " §7- " + c.description());
                }
            }
            player.sendMessage("§7----[ §9Manager Commands §7]----");
            for(Command c : commands.keySet()) {
                if(commands.get(c).managerCommand() && player.hasPermission(c.permission())) {
                    player.sendMessage("§7/" + label + " §9" + c.name() + " §7- " + c.description());
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
            player.sendMessage("§7Unknown team command! Use /§9" + label + " §7for help on teams!");
            return true;
        }

        if(commands.get(cmd).managerCommand() && Avalon.get().getTeamManager().getPlayerTeam(player) == null) {
            player.sendMessage("§cYou are not on a team!");
            return true;
        }

        if(commands.get(cmd).managerCommand() && !Avalon.get().getTeamManager().getPlayerTeam(player).isManager(player.getUniqueId())) {
            player.sendMessage("§cYou must be a manager to use this command!");
            return true;
        }

        if(cmd.permission() != "" && !player.hasPermission(cmd.permission())) {
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
