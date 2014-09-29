package me.aventium.avalon.team;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import me.aventium.avalon.Avalon;
import me.aventium.avalon.serialization.JSONLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

public class Team {

    private String name;
    private String password;
    private Map<UUID, String> members;
    private Map<UUID, String> managers;
    private boolean friendlyFire;
    public JSONLocation hq, rally;
    public boolean remove = false;

    public Team(final String name) {
        this.name = name;

        Jedis jedis = Avalon.get().getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();

        Response<String> passResponse = pipeline.hget("teams:" + name, "password");
        Response<String> membersResponse = pipeline.hget("teams:" + name, "members");
        Response<String> managersResponse = pipeline.hget("teams:" + name, "managers");
        Response<String> friendlyFireResponse = pipeline.hget("teams:" + name, "friendlyfire");
        Response<String> hqResponse = pipeline.hget("teams:" + name, "hq");
        Response<String> rallyResponse = pipeline.hget("teams:" + name, "rally");

        pipeline.sync();

        this.password = passResponse.get();

        this.members = Maps.newConcurrentMap();
        this.managers = Maps.newConcurrentMap();

        if (membersResponse != null && membersResponse.get() != null && membersResponse.get().length() != 0) {
            for (String string : membersResponse.get().split(",")) {
                members.put(UUID.fromString(string.split(":")[0]), string.split(":")[1]);
            }
        }

        if (managersResponse != null && managersResponse.get() != null && managersResponse.get().length() != 0) {
            for (String string : managersResponse.get().split(",")) {
                managers.put(UUID.fromString(string.split(":")[0]), string.split(":")[1]);
            }
        }

        this.friendlyFire = Boolean.valueOf(friendlyFireResponse.get());

        if (hqResponse != null && hqResponse.get() != null && !hqResponse.get().equalsIgnoreCase("null")) {
            try {
                this.hq = new JSONLocation(new JSONObject(hqResponse.get()));
            } catch (JSONException ex) {
                System.out.println("Error while reading JSON HQ value!");
                ex.printStackTrace();
            }
        }
        if (rallyResponse != null && rallyResponse.get() != null && !rallyResponse.get().equalsIgnoreCase("null")) {
            try {
                this.rally = new JSONLocation(new JSONObject(rallyResponse.get()));
            } catch (JSONException ex) {
                System.out.println("Error while reading JSON Rally value!");
                ex.printStackTrace();
            }
        }
        Avalon.get().getJedisPool().returnResource(jedis);
    }

    public Team(String name, UUID founderUUID, String founderName) {
        this.name = name;
        this.password = "none";
        this.friendlyFire = false;
        managers = new HashMap<>();
        managers.put(founderUUID, founderName);
        members = new HashMap<>();
        hq = null;
        rally = null;
        save();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPass) {
        this.password = newPass;
        save();
    }

    public Map<UUID, String> getMembers() {
        return members;
    }

    public void addMember(UUID uuid, String username) {
        members.put(uuid, username);
        save();
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
        save();
    }

    public void removeMember(String username) {
        for (UUID id : members.keySet()) {
            if (members.get(id).equalsIgnoreCase(username)) {
                members.remove(id);
                return;
            }
        }
        save();
    }

    public Map<UUID, String> getManagers() {
        return managers;
    }

    public void addManager(UUID uuid, String username) {
        members.remove(uuid);
        managers.put(uuid, username);
        save();
    }

    public boolean isManager(UUID uuid) {
        return managers.containsKey(uuid);
    }

    public boolean isManager(String username) {
        return managers.containsValue(username.toLowerCase());
    }

    public void removeManager(UUID uuid) {
        managers.remove(uuid);
        save();
    }

    public void removeManager(String username) {
        for (UUID id : managers.keySet()) {
            if (managers.get(id).equalsIgnoreCase(username)) {
                managers.remove(id);
                return;
            }
        }
        save();
    }

    public boolean friendlyFireOn() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean on) {
        this.friendlyFire = on;
        save();
    }

    public Location getHQ() {
        return this.hq == null ? null : this.hq.toLocation();
    }

    public void setHQ(Location newHQ) {
        this.hq = new JSONLocation(newHQ);
        save();
    }

    public Location getRally() {
        return this.rally == null ? null : this.rally.toLocation();
    }

    public void setRally(Location newRally) {
        this.rally = new JSONLocation(newRally);
        save();
    }

    public boolean hasPlayer(String name) {
        boolean found = false;
        for (String str : members.values()) {
            if (str.equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }
        for (String str : managers.values()) {
            if (str.equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void removePlayer(UUID uuid) {
        if (members.containsKey(uuid)) members.remove(uuid);
        if (managers.containsKey(uuid)) managers.remove(uuid);
        save();
    }

    public void message(String message) {
        for (UUID id : members.keySet()) {
            if (Bukkit.getPlayer(id) != null) Bukkit.getPlayer(id).sendMessage(message);
        }
        for (UUID id : managers.keySet()) {
            if (Bukkit.getPlayer(id) != null) Bukkit.getPlayer(id).sendMessage(message);
        }
    }

    public void save() {
        new BukkitRunnable() {
            public void run() {
                Jedis jedis = Avalon.get().getJedisPool().getResource();

                if (remove) {
                    jedis.del("teams:" + name);
                    for(UUID id : members.keySet()) {
                        Avalon.get().getTeamManager().setTeam(id, null);
                    }

                    for(UUID id : managers.keySet()) {
                        Avalon.get().getTeamManager().setTeam(id, null);
                    }
                    Avalon.get().getJedisPool().returnResource(jedis);
                    Avalon.get().getTeamManager().teams.remove(name);
                    return;
                }

                jedis.hset("teams:" + name, "password", password);

                List<String> membersA = new ArrayList<>(), managersA = new ArrayList<>();

                for (UUID id : members.keySet()) {
                    membersA.add(id.toString() + ":" + members.get(id));
                }

                for (UUID id : managers.keySet()) {
                    managersA.add(id.toString() + ":" + managers.get(id));
                }

                jedis.hset("teams:" + name, "members", Joiner.on('\t').join(membersA));
                jedis.hset("teams:" + name, "managers", Joiner.on('\t').join(managersA));
                jedis.hset("teams:" + name, "friendlyfire", Boolean.valueOf(friendlyFire).toString());
                jedis.hset("teams:" + name, "hq", (hq == null ? "null" : hq.toJSON().toString()));
                jedis.hset("teams:" + name, "rally", (rally == null ? "null" : rally.toJSON().toString()));
                Avalon.get().getJedisPool().returnResource(jedis);
            }
        }.runTaskAsynchronously(Avalon.get());
    }
}
