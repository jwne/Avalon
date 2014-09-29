package me.aventium.avalon.team;

import me.aventium.avalon.Avalon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {

    public Map<String, Team> teams = new ConcurrentHashMap<>();

    public Team getTeam(String name) {
        if(name == null) return null;
        if(teams.containsKey(name)) {
            return teams.get(name);
        }

        Jedis jedis = Avalon.get().getJedisPool().getResource();

        Pipeline pipeline = jedis.pipelined();

        Response<String> teamResponse = pipeline.hget("teams:" + name, "founder-name");

        pipeline.sync();

        Avalon.get().getJedisPool().returnResource(jedis);

        if(teamResponse.get() == null) return null;

        Team team = new Team(name);
        teams.put(team.getName(), team);
        return team;
    }

    public Team getPlayerTeam(Player player) {
        return getPlayerTeam(player.getUniqueId());
    }

    public Team getPlayerTeam(UUID uuid) {
        Team team = null;
        for(Team t : teams.values()) {
            if(t.getMembers().containsKey(uuid)) {
                team = t;
                break;
            } else if(t.getManagers().containsKey(uuid)) {
                team = t;
                break;
            }
        }
        if(team != null) return team;

        Jedis jedis = Avalon.get().getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();

        Response<String> teamResponse = pipeline.hget("playerteams", uuid.toString());

        pipeline.sync();

        String teamName = teamResponse.get();

        team = getTeam(teamName);

        Avalon.get().getJedisPool().returnResource(jedis);

        return team;
    }

    public void setTeam(final Player player, final Team team) {
        new BukkitRunnable() {
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();
                if(team == null) {
                    jedis.hdel("playerteams", player.getUniqueId().toString());
                } else {
                    jedis.hset("playerteams", player.getUniqueId().toString(), team.getName());
                }
                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }

    public void setTeam(final UUID player, final Team team) {
        new BukkitRunnable() {
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();
                if(team == null) {
                    jedis.hdel("playerteams", player.toString());
                } else {
                    jedis.hset("playerteams", player.toString(), team.getName());
                }
                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }

    public void addTeam(Team team) {
        teams.put(team.getName(), team);
    }

    public void save() {
        for(Team team : teams.values()) {
            team.save();
        }
    }
}
