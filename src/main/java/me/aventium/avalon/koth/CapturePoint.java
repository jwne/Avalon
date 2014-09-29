package me.aventium.avalon.koth;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.koth.event.CapturingTeamChangeEvent;
import me.aventium.avalon.koth.event.CapturingTimeChangeEvent;
import me.aventium.avalon.koth.event.PointOwnerChangeEvent;
import me.aventium.avalon.koth.event.PointStatusChangeEvent;
import me.aventium.avalon.regions.Region;
import me.aventium.avalon.team.Team;
import me.aventium.avalon.utils.Chat;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CapturePoint {

    protected final CapturePointDefinition definition;
    protected final PlayerTracker tracker;

    protected final Vector centerPoint;

    protected boolean active = false;

    protected Team capturingTeam = null;
    protected Team owningTeam = null;

    protected long capturingTime = 0L;

    public CapturePoint(CapturePointDefinition definition) {
        this.definition = definition;

        this.centerPoint = getCaptureRegion().getBounds().getCenterPoint();
        this.tracker = new PlayerTracker(getCaptureRegion());
    }

    /**
     * register listeners
     */
    public void register() {
        Avalon.get().registerEvents(tracker);
    }

    /**
     * unregister all listeners
     */
    public void unregister() {
        HandlerList.unregisterAll(tracker);
    }

    /**
     * @return the definition of the point
     */
    public CapturePointDefinition getDefinition() {
        return definition;
    }

    /**
     * @return the tracker for checking players that are inside the capture region
     */
    public PlayerTracker getPlayerTracker() {
        return tracker;
    }

    /**
     * @return the region that contains the point
     */
    public Region getCaptureRegion() {
        return definition.getCaptureRegion();
    }

    /**
     * @return the time needed capturing to capture the point
     */
    public long getTimeToCapture() {
        return definition.getTimeToCapture();
    }

    /**
     * @return the name of the point
     */
    public String getName() {
        return definition.getName();
    }

    /**
     * @return whether or not the point is active
     */
    public boolean isActive() { return active; }

    /**
     * Set whether or not the capture point is active
     * @param a if the point will be active or not
     */
    public void setActive(boolean a) {
        Avalon.get().callEvent(new PointStatusChangeEvent(this, active, a));
        this.active = a;
    }

    /**
     * Point used for broadcasting and telling players the location of the point.
     */
    public Vector getCenterPoint() {
        return centerPoint.clone();
    }

    /**
     * @return the team, if any, that owns the point
     */
    public Team getOwningTeam() {
        return owningTeam;
    }

    /**
     * @return the team, if any, that is capturing the point
     */
    public Team getCapturingTeam() {
        return capturingTeam;
    }

    /**
     * @return the progress of capturing the point for the current capturingTeam
     */
    public long getCapturingTime() {
        return capturingTime;
    }

    /**
     * @return the progress of capturing the point for the current capturingTeam from 0 to 1(for %)
     */
    public double getCompletion() {
        return (double) capturingTime / (double) definition.getTimeToCapture();
    }

    public boolean isCompleted(Team team) {
        return owningTeam != null && owningTeam == team;
    }

    public void tick(long duration) {
        Map<Team, Integer> playerCounts = new HashMap<>();

        Team leading = null;

        int defenders = 0;

        for(UUID id : tracker.getPlayersOnPoint()) {
            Team team = Avalon.get().getTeamManager().getPlayerTeam(id);
            if(team != null) {
                defenders++;
                int playerCount = 0;
                if(playerCounts.containsKey(team)) playerCount = playerCounts.get(team) + 1;
                else playerCount = 1;
                playerCounts.put(team, playerCount);
                if(team != leading) {
                    if(leading == null || playerCount > playerCounts.get(leading)) {
                        leading = team;
                    }
                }
            }
        }

        int lead = 0;

        if(leading != null) {
            lead = playerCounts.get(leading);
            defenders -= lead;

            switch(this.definition.getCaptureCondition()) {
                case EXCLUSIVE:
                    if(defenders > 0) {
                        lead = 0;
                    }
                    break;
                case MAJORITY:
                    lead = Math.max(0, lead - defenders);
                    break;
                case LEAD:
                    break;
            }
        }

        if(lead > 0) {
            if(this.getDefinition().scaleTime()) {
                duration = duration * lead;
            }
            captureAndFireEvents(leading, duration);
        } else {
            captureAndFireEvents(null, duration);
        }
    }

    private void captureAndFireEvents(@Nullable Team team, long capturingTime) {
        long oldCapTime = this.capturingTime;
        Team oldCapTeam = this.capturingTeam;
        Team oldOwningTeam = this.owningTeam;

        capture(team, capturingTime);

        if(!(oldCapTime == this.capturingTime) && (this.capturingTime - oldCapTime >= 20)) {
            Avalon.get().callEvent(new CapturingTimeChangeEvent(this));
        }

        if(oldCapTeam != this.capturingTeam) {
            Avalon.get().callEvent(new CapturingTeamChangeEvent(this, oldCapTeam, this.capturingTeam));
        }

        if(oldOwningTeam != this.owningTeam) {
            Avalon.get().callEvent(new PointOwnerChangeEvent(this, oldOwningTeam, this.owningTeam));
        }
    }


    /**
     * Start capturing the point with a different team
     * @param team team capturing
     * @param capturingTime capture duration
     */
    private void capture(Team team, long capturingTime) {
        if(!this.active || !(capturingTime > 0)) {
            return;
        }

        CapturePointDefinition definition = this.getDefinition();

        if(team != null && capturingTeam != team) team.message(Chat.BASE_COLOR + "You are now trying to capture " + Chat.IMPORTANT_COLOR + this.getName() + Chat.BASE_COLOR + "!");

        if(this.capturingTeam != null) {
            if(team == capturingTeam) {
                progressCapture(team, capturingTime);
            }
        }

        if(team != null && team != this.owningTeam) {
            this.capturingTeam = team;
            progressCapture(team, capturingTime);
        }
    }

    /**
     * Progress to capturing the point
     * @param team team capturing
     * @param capturingTime capture duration
     */
    private void progressCapture(Team team, long capturingTime) {
        this.capturingTime = this.capturingTime += capturingTime;
        Avalon.get().callEvent(new CapturingTimeChangeEvent(this));
        if(!(this.capturingTime < this.definition.getTimeToCapture())) {
            this.capturingTime = 0L;
            Avalon.get().callEvent(new PointOwnerChangeEvent(this, this.owningTeam, this.capturingTeam));
            this.owningTeam = this.capturingTeam;
            this.capturingTeam = null;
            this.active = false;
        }
    }



}
