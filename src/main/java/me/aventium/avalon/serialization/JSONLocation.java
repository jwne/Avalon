package me.aventium.avalon.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONLocation {

    private World world;
    private double x, y, z;
    private float yaw, pitch;

    public JSONLocation(Location location) {
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public JSONLocation(JSONObject jsonObject) {
        try {
            this.world = Bukkit.getWorld(jsonObject.getString("world"));
            this.x = jsonObject.getDouble("x");
            this.y = jsonObject.getDouble("y");
            this.z = jsonObject.getDouble("z");
            this.yaw = jsonObject.getInt("yaw");
            this.pitch = jsonObject.getInt("pitch");
        } catch(JSONException ex) {
            System.out.println("Error reading location from JSON!");
            ex.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("world", world.getName());
            jsonObject.put("x", x);
            jsonObject.put("y", y);
            jsonObject.put("z", z);
            jsonObject.put("yaw", yaw);
            jsonObject.put("pitch", pitch);
            return jsonObject;
        } catch(JSONException ex) {
            System.out.println("Error saving location to JSON!");
            ex.printStackTrace();
        }
        return null;
    }

    public Location toLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

}
