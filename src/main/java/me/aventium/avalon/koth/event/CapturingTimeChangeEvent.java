package me.aventium.avalon.koth.event;

import me.aventium.avalon.koth.CapturePoint;
import org.bukkit.event.HandlerList;

public class CapturingTimeChangeEvent extends CapturePointEvent {

    private static final HandlerList handlers = new HandlerList();

    public CapturingTimeChangeEvent(CapturePoint capturePoint) {
        super(capturePoint);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
