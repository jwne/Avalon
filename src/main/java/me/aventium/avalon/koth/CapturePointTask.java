package me.aventium.avalon.koth;

import me.aventium.avalon.Avalon;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class CapturePointTask implements Runnable {

    private static final long INTERVAL = 20L;

    private final List<CapturePoint> capturePoints;
    private BukkitTask task;

    public CapturePointTask(List<CapturePoint> capturePoints) {
        this.capturePoints = capturePoints;
    }

    public void start() {
        this.task = Avalon.get().getServer().getScheduler().runTaskTimer(Avalon.get(), this, 0, INTERVAL);
    }

    public void stop() {
        if(this.task != null) {
            this.task.cancel();
        }
    }

    @Override
    public void run() {
        for(CapturePoint cp : capturePoints) {
            cp.tick(INTERVAL);
        }
    }

}
