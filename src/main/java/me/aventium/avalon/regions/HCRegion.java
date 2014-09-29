package me.aventium.avalon.regions;

import me.aventium.avalon.Avalon;
import org.bukkit.event.HandlerList;

public class HCRegion {

    private final String name;

    private final Region region;

    private final boolean deathban;

    private final RegionPlayerTracker tracker;

    public HCRegion(String name, Region region, boolean deathban) {
        this.name = name;
        this.region = region;
        this.deathban = deathban;

        this.tracker = new RegionPlayerTracker(this);
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    public boolean isDeathban() {
        return deathban;
    }

    public void register() {
        Avalon.get().registerEvents(this.tracker);
    }

    public void unregister() {
        HandlerList.unregisterAll(this.tracker);
    }

}
