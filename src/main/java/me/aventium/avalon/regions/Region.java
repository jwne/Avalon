package me.aventium.avalon.regions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.json.JSONObject;

public abstract class Region {

    /**
     * Parse a region from JSON
     * @param paramJSONObject the object to read from
     * @return The region that was parsed
     */
    public abstract Region fromJSON(JSONObject paramJSONObject);

    /**
     * Save a region to JSON
     * @return the json the region was saved to
     */
    public abstract JSONObject toJSON();

    /**
     * Check for a given point in the region
     * @param paramVector the point to check for
     * @return if the point exists in the region
     */
    public abstract boolean contains(Vector paramVector);

    /**
     * Check for a given point in the region
     * @param paramLocation the point to check for
     * @return if the point exists in the region
     */
    public boolean contains(Location paramLocation) {
        return contains(paramLocation.toVector());
    }

    /**
     * Check for a given point in the region
     * @param paramBlock the point to check for
     * @return if the point exists in the region
     */
    public boolean contains(Block paramBlock) {
        return contains(paramBlock.getLocation().toVector().add(new Vector(0.5, 0.5, 0.5)));
    }


    /**
     * Check for a given entity in the region
     * @param paramEntity the entity to check for
     * @return if the entity exists in the region
     */
    public boolean contains(Entity paramEntity) {
        return contains(paramEntity.getLocation().toVector());
    }

    /**
     * Check if the region has bounds.
     * @return whether or not the region has bounds
     */
    public abstract boolean isBlockBounded();

    /**
     * @return The smallest possible bounds that contains the region.
     */
    public abstract Bounds getBounds();

}
