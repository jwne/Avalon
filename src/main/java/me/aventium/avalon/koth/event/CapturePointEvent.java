package me.aventium.avalon.koth.event;

import me.aventium.avalon.koth.CapturePoint;
import org.bukkit.event.Event;

public abstract class CapturePointEvent extends Event {

    protected final CapturePoint capturePoint;

    public CapturePointEvent(CapturePoint capturePoint) {
        this.capturePoint = capturePoint;
    }

    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

}
