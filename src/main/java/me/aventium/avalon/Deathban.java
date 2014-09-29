package me.aventium.avalon;

import me.aventium.avalon.utils.TimeUtils;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.UUID;

public class Deathban implements Listener, CommandExecutor {

    public boolean isDeathbanned(UUID player) {
        Jedis jedis = Avalon.get().getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();

        Response<String> uuidResponse = pipeline.hget("deathban", player.toString());

        pipeline.sync();

        Avalon.get().getJedisPool().returnResource(jedis);

        return (uuidResponse.get() != null && uuidResponse.get() != "" && uuidResponse.get() != "0");
    }

    public long getDeathbanTime(UUID player) {
        Jedis jedis = Avalon.get().getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();

        Response<String> uuidResponse = pipeline.hget("deathban", player.toString());

        pipeline.sync();

        Avalon.get().getJedisPool().returnResource(jedis);

        if(uuidResponse.get() != null && uuidResponse.get() != "" && uuidResponse.get() != "0") {
            return Long.valueOf(uuidResponse.get());
        }
        return 0L;
    }

    public void addDeathban(final UUID player, final long expires) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();

                jedis.hset("deathban", player.toString(), String.valueOf(expires));

                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }

    public void removeDeathban(final UUID player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();

                jedis.hdel("deathban", player.toString());

                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("deathban.remove")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage("§cYou must supply a player to remove a deathban!");
            return true;
        }

        String name = args[0];

        UUID id = Avalon.get().getPlayerManager().getPlayerUUID(name);

        if(id == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if(!isDeathbanned(id)) {
            sender.sendMessage("§cThat player is not currently deathbanned!");
            return true;
        }

        removeDeathban(id);
        sender.sendMessage("§aDeathban removed.");
        return true;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getEntity().getLocation().getWorld().strikeLightning(event.getEntity().getLocation());
        if(event.getEntity().getKiller() != null) event.setDeathMessage("§c" + event.getEntity().getName() + " §6slain by §c" + event.getEntity().getKiller().getName());
        else event.setDeathMessage(null);

        if(!event.getEntity().hasPermission("deathban.bypass")) {
            addDeathban(event.getEntity().getUniqueId(), Avalon.get().getConfig().getInt("deathban-time") * 20L);
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID id = event.getUniqueId();

        if(isDeathbanned(id)) {
            long expires = getDeathbanTime(id);

            if(System.currentTimeMillis() >= expires) {
                removeDeathban(id);
                event.allow();
            } else {
                String time = TimeUtils.formatDateDiff(expires * 1000);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§6You are deathbanned for another §c" + time + "§6.");
            }
        }
    }

}
