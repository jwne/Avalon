package me.aventium.avalon.koth.event;

import me.aventium.avalon.koth.CapturePoint;
import org.bukkit.event.HandlerList;

public class PointStatusChangeEvent extends CapturePointEvent {

    private static final HandlerList handlers = new HandlerList();
    private boolean oldStatus, newStatus;

    public PointStatusChangeEvent(CapturePoint point, boolean oldStatus, boolean newStatus) {
        super(point);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean getNewStatus() {
        return newStatus;
    }

    public boolean getOldStatus() {
        return oldStatus;
    }

}
