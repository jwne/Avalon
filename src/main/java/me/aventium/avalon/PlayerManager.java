package me.aventium.avalon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.UUID;

public class PlayerManager implements Listener {

    public UUID getPlayerUUID(String name) {
        Jedis jedis = Avalon.get().getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();

        Response<String> uuidResponse = pipeline.hget("uuids", name.toLowerCase());

        pipeline.sync();

        Avalon.get().getJedisPool().returnResource(jedis);

        return UUID.fromString(uuidResponse.get());
    }

    public void updateUUID(final String name, final UUID uuid) {

        new BukkitRunnable() {
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();
                jedis.hset("uuids", name.toLowerCase(), uuid.toString());
                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateUUID(event.getPlayer().getName(), event.getPlayer().getUniqueId());
    }

}
