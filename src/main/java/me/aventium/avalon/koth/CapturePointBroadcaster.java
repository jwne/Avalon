package me.aventium.avalon.koth;

import me.aventium.avalon.koth.event.CapturingTeamChangeEvent;
import me.aventium.avalon.koth.event.CapturingTimeChangeEvent;
import me.aventium.avalon.koth.event.PointOwnerChangeEvent;
import me.aventium.avalon.koth.event.PointStatusChangeEvent;
import me.aventium.avalon.utils.Chat;
import me.aventium.avalon.utils.TimeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CapturePointBroadcaster implements Listener {

    // 50 milliseconds = 1 tick, 20 ticks = 1 second

    private int inSeconds(long millis) {
        int ticks = (int) millis / 50;
        return ticks / 20;

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOwnerChange(PointOwnerChangeEvent event) {
        if(event.getCapturePoint().isActive()) {
            if(event.getOldOwner() != null) {
                Chat.broadcast(event.getOldOwner().getName() +
                Chat.BASE_COLOR + " lost control of " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getName() +
                Chat.BASE_COLOR + "!");
            }

            if(event.getNewOwner() != null) {
                Chat.broadcast(event.getNewOwner().getName() +
                Chat.BASE_COLOR + " captured " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getName() +
                Chat.BASE_COLOR + "!");
                event.getCapturePoint().setActive(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCapteamChange(CapturingTeamChangeEvent event) {
        if(event.getCapturePoint().isActive()) {
            if(event.getOldTeam() != null) {
                Chat.broadcast(event.getOldTeam().getName() +
                Chat.BASE_COLOR + " lost control of " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getName() +
                Chat.BASE_COLOR + "!");
            }

            if(event.getNewTeam() != null) {
                Chat.broadcast(event.getNewTeam().getName() +
                Chat.BASE_COLOR + " has started capturing " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getName() +
                Chat.BASE_COLOR + "! [" + Chat.IMPORTANT_COLOR +
                TimeUtils.formatTimeLeft(event.getCapturePoint().getTimeToCapture() - event.getCapturePoint().getCapturingTime()) +
                Chat.BASE_COLOR + "]");
            }
        }
    }

    private boolean shouldBroadcast(long timeLeft, long fullTime) {
        return ((timeLeft <= 200) ||
                (timeLeft <= 2400 && timeLeft % 600 == 0) ||
                (timeLeft % 1200 == 0));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCaptimeChange(CapturingTimeChangeEvent event) {
        if(shouldBroadcast(event.getCapturePoint().getCapturingTime(), event.getCapturePoint().getTimeToCapture())) {
            Chat.broadcast(event.getCapturePoint().getCapturingTeam().getName() +
                    Chat.BASE_COLOR + " is capturing " +
                    Chat.IMPORTANT_COLOR + event.getCapturePoint().getName() +
                    Chat.BASE_COLOR + "! [" + Chat.IMPORTANT_COLOR +
                    TimeUtils.formatTimeLeft(event.getCapturePoint().getTimeToCapture() - event.getCapturePoint().getCapturingTime()) +
                    Chat.BASE_COLOR + "]");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStatusChange(PointStatusChangeEvent event) {
        Chat.broadcast(event.getCapturePoint().getName() +
            Chat.BASE_COLOR + "[" +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getCenterPoint().getX() + Chat.BASE_COLOR + ", " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getCenterPoint().getY() + Chat.BASE_COLOR + ", " +
                Chat.IMPORTANT_COLOR + event.getCapturePoint().getCenterPoint().getZ() + Chat.BASE_COLOR + "] is now " +
            Chat.IMPORTANT_COLOR + (event.getNewStatus() ? "active" : "inactive") +
            Chat.BASE_COLOR + "!");
    }

}
