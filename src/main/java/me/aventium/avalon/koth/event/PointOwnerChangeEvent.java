package me.aventium.avalon.koth.event;

import me.aventium.avalon.team.Team;
import me.aventium.avalon.koth.CapturePoint;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class PointOwnerChangeEvent extends CapturePointEvent {

    private static final HandlerList handlers = new HandlerList();
    @Nullable private final Team oldOwner;
    @Nullable private final Team newOwner;

    public PointOwnerChangeEvent(CapturePoint point, Team oldOwner, Team newOwner) {
        super(point);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nullable
    public Team getNewOwner() {
        return newOwner;
    }

    @Nullable
    public Team getOldOwner() {
        return oldOwner;
    }

}
