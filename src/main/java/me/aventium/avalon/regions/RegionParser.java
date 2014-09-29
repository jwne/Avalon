package me.aventium.avalon.regions;

import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

public class RegionParser {

    public static CuboidRegion parseCuboidRegion(JSONObject jsonObject) {
        try {
            JSONObject minObject = jsonObject.getJSONObject("min");
            JSONObject maxObject = jsonObject.getJSONObject("max");
            Vector min = new Vector(minObject.getDouble("x"), minObject.getDouble("y"), minObject.getDouble("z"));
            Vector max = new Vector(maxObject.getDouble("x"), maxObject.getDouble("y"), maxObject.getDouble("z"));
            return new CuboidRegion(min, max);
        } catch(JSONException ex) {
            System.out.println("Error while parsing cuboid region from file!");
            ex.printStackTrace();
        }
        return null;
    }

}
