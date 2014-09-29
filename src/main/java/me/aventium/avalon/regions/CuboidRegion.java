package me.aventium.avalon.regions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class CuboidRegion extends Region {

    private final Bounds bounds;

    public CuboidRegion(Vector pos1, Vector pos2) {
        this.bounds = new Bounds(null, Vector.getMinimum(pos1, pos2), Vector.getMaximum(pos1, pos2));
    }

    @Override
    public CuboidRegion fromJSON(JSONObject jsonObject) {
        if(!jsonObject.has("bounds")) {
            return null;
        }

        try {
            JSONObject jsonBounds = jsonObject.getJSONObject("bounds");
            if(!jsonBounds.has("world") || !jsonBounds.has("min") || !jsonBounds.has("max")) return null;

            World world = Bukkit.getWorld(jsonObject.getString("world"));
            if(world == null) return null;
            JSONObject jsonMin = jsonBounds.getJSONObject("min");
            JSONObject jsonMax = jsonBounds.getJSONObject("max");

            Vector min = new Vector(jsonMin.getDouble("x"), jsonMin.getDouble("y"), jsonMin.getDouble("z"));
            Vector max = new Vector(jsonMax.getDouble("x"), jsonMax.getDouble("y"), jsonMax.getDouble("z"));
            CuboidRegion r = new CuboidRegion(min, max);
            r.getBounds().setWorld(world);
            return r;
        } catch(JSONException ex) {
            System.out.println("Error parsing JSON!");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("world", bounds.getWorld().getName());

            JSONObject jsonBounds = new JSONObject();

            JSONObject jsonMin = new JSONObject();
            JSONObject jsonMax = new JSONObject();

            jsonMin.put("x", bounds.getMin().getX());
            jsonMin.put("y", bounds.getMin().getY());
            jsonMin.put("z", bounds.getMin().getZ());

            jsonMax.put("x", bounds.getMax().getX());
            jsonMax.put("y", bounds.getMax().getY());
            jsonMax.put("z", bounds.getMax().getZ());

            jsonBounds.put("min", jsonMin);
            jsonBounds.put("max", jsonMax);

            jsonObject.put("bounds", jsonBounds);
            return jsonObject;
        } catch(JSONException ex) {
            System.out.println("Error saving cuboid region to JSON!");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean contains(Vector paramVector) {
        return this.bounds.contains(paramVector);
    }

    @Override
    public boolean isBlockBounded() {
        return true;
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    public Vector getRandomPoint(Random random) {
        if(bounds.isEmpty()) {
            throw new ArithmeticException("Region empty");
        }

        double x = randRange(random, bounds.min.getX(), bounds.max.getX());
        double y = randRange(random, bounds.min.getY(), bounds.max.getY());
        double z = randRange(random, bounds.min.getZ(), bounds.max.getZ());
        return new Vector(x, y, z);
    }

    private double randRange(Random random, double min, double max) {
        return (max - min) * random.nextDouble() + min;
    }

    @Override
    public String toString() {
        return "Cuboid{min=[" + bounds.min.toString() + "],max=[" + bounds.max.toString() + "]}";
    }
}
