package me.aventium.avalon.koth;

import me.aventium.avalon.regions.Region;

public class CapturePointDefinition {

    // The region that players have to be on to capture
    private final Region captureRegion;

    // The name of the capture point
    private final String name;

    // The amount of time capturing needed to capture
    private final long timeToCapture;

    // Whether or not to speed up capturing depending on the amount of players capturing
    private final boolean scaleTime;

    public static enum CaptureCondition {
        EXCLUSIVE, // Team owns every player on the capture point
        MAJORITY, // Team owns over half of the players on the capture point
        LEAD // Team owns most players on the point than any other team
    }

    // The condition needed to capture
    private final CaptureCondition captureCondition;

    public CapturePointDefinition(String name, Region captureRegion, long timeToCapture, boolean scaleTime, CaptureCondition captureCondition) {
        this.name = name;
        this.captureRegion = captureRegion;
        this.timeToCapture = timeToCapture;
        this.scaleTime = scaleTime;
        this.captureCondition = captureCondition;
    }

    public String getName() {
        return name;
    }

    public Region getCaptureRegion() {
        return captureRegion;
    }

    public long getTimeToCapture() {
        return timeToCapture;
    }

    public boolean scaleTime() {
        return scaleTime;
    }

    public CaptureCondition getCaptureCondition() {
        return captureCondition;
    }

}
