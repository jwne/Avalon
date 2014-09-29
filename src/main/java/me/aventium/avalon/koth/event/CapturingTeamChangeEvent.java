package me.aventium.avalon.koth.event;

import me.aventium.avalon.team.Team;
import me.aventium.avalon.koth.CapturePoint;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class CapturingTeamChangeEvent extends CapturePointEvent {

    private static final HandlerList handlers = new HandlerList();
    @Nullable private final Team oldTeam;
    @Nullable private final Team newTeam;

    public CapturingTeamChangeEvent(CapturePoint capturePoint, Team oldTeam, Team newTeam) {
        super(capturePoint);
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public @Nullable Team getOldTeam() {
        return this.oldTeam;
    }

    public @Nullable Team getNewTeam() {
        return this.newTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
