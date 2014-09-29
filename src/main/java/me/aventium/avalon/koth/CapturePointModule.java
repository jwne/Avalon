package me.aventium.avalon.koth;

import com.google.common.collect.ImmutableList;
import me.aventium.avalon.Avalon;
import me.aventium.avalon.editing.EditSession;
import me.aventium.avalon.editing.EditSessions;
import me.aventium.avalon.editing.Editors;
import me.aventium.avalon.editing.editors.KOTHEditor;
import me.aventium.avalon.koth.event.PointStatusChangeEvent;
import me.aventium.avalon.regions.CuboidRegion;
import me.aventium.avalon.regions.Region;
import me.aventium.avalon.regions.RegionParser;
import me.aventium.avalon.utils.ParsingUtils;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CapturePointModule {

    private final List<CapturePoint> capturePoints = new ArrayList<>();
    private final CapturePointTask tickTask;
    private final CapturePointBroadcaster broadcaster;

    public CapturePointModule(ImmutableList<CapturePoint> points) {
        if(points != null) this.capturePoints.addAll(points);

        this.tickTask = new CapturePointTask(this.capturePoints);
        this.broadcaster = new CapturePointBroadcaster();
    }

    /**
     * @return a list of capture points
     */
    public List<CapturePoint> getCapturePoints() {
        return capturePoints;
    }

    public boolean pointExists(String name) {
        for(CapturePoint point : capturePoints) {
            if(point.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void createNewPoint(String name, Player player, long capTime) {
        if(EditSessions.getSession(player, Editors.getEditor(Material.GOLD_HOE)) == null) {
            player.sendMessage("§cYou must select two points for the region with a gold hoe!");
            return;
        }

        EditSession session = EditSessions.getSession(player, Editors.getEditor(Material.GOLD_HOE));
        KOTHEditor editor = (KOTHEditor) session.getEditor();

        Vector p1 = editor.getPos1();
        Vector p2 = editor.getPos2();

        CuboidRegion region = new CuboidRegion(p1, p2);
        region.getBounds().setWorld(player.getWorld());
        CapturePointDefinition definition = new CapturePointDefinition(name, region, capTime, true, CapturePointDefinition.CaptureCondition.MAJORITY);
        CapturePoint cp = new CapturePoint(definition);
        capturePoints.add(cp);
        cp.register();
        saveCapturePoints(new File(Avalon.get().getDataFolder(), "capturepoints.json"));
        player.sendMessage("§6Point created!");
    }

    public CapturePoint getCapturePoint(String name) {
        for(CapturePoint cp : capturePoints) {
            if(cp.getName().equalsIgnoreCase(name)) return cp;
        }
        return null;
    }

    public void activateCapturePoint(CapturePoint point) {
        int found = -1;
        for(int i = 0; i < capturePoints.size(); i++) {
            if(capturePoints.get(i).getName().equalsIgnoreCase(point.getName())) {
                found = i;
                break;
            }
        }
        if(found == -1) return;
        if(capturePoints.get(found).isActive()) return;
        boolean old = capturePoints.get(found).isActive();
        capturePoints.get(found).setActive(true);
        Avalon.get().callEvent(new PointStatusChangeEvent(capturePoints.get(found), old, true));
    }

    public void deactivateCapturePoint(CapturePoint point) {
        int found = -1;
        for(int i = 0; i < capturePoints.size(); i++) {
            if(capturePoints.get(i).getName().equalsIgnoreCase(point.getName())) {
                found = i;
                break;
            }
        }
        if(found == -1) return;
        if(!capturePoints.get(found).isActive()) return;
        boolean old = capturePoints.get(found).isActive();
        capturePoints.get(found).setActive(false);
        Avalon.get().callEvent(new PointStatusChangeEvent(capturePoints.get(found), old, false));
    }

    /**
     * Load the module, call before CapturePointModule#enable()
     */
    public void load() {
        Avalon.get().registerEvents(broadcaster);


        List<CapturePointDefinition> definitions = parseDefinitions(new File(Avalon.get().getDataFolder(), "capturepoints.json"));
        if(definitions == null || definitions.size() == 0) {
            System.out.println("No capture points loaded!");
        } else {
            for(CapturePointDefinition definition : definitions) {
                CapturePoint capturePoint = new CapturePoint(definition);
                capturePoints.add(capturePoint);
            }
            System.out.println(definitions.size() + " capture points loaded!");
        }

        for(CapturePoint cp : capturePoints) {
            cp.register();
        }
    }

    /**
     * Unload the module, call after CapturePointModule#disable()
     */
    public void unload() {
        saveCapturePoints(new File(Avalon.get().getDataFolder(), "capturepoints.json"));
        for(CapturePoint cp : capturePoints) {
            cp.unregister();
        }

        HandlerList.unregisterAll(broadcaster);
    }

    /**
     * Enable the module, call after CapturePointModule#load()
     */
    public void enable() {
        tickTask.start();
    }

    /**
     * Disable the module, call before CapturePointModule#unload()
     */
    public void disable() {
        tickTask.stop();
    }

    /**
     * Save all capture points to file
     * @param file the file to save capture points to
     */
    private void saveCapturePoints(File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException ex) {
                System.out.println("Error while creating file: " + file.getName() + "!");
                ex.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray pointArray = new JSONArray();
            for(CapturePoint capturePoint : capturePoints) {
                JSONObject pointObject = new JSONObject();
                pointObject.put("name", capturePoint.getName());
                pointObject.put("captureTime", capturePoint.getTimeToCapture());
                pointObject.put("scale-time", capturePoint.getDefinition().scaleTime());
                pointObject.put("capture-rule", capturePoint.getDefinition().getCaptureCondition().toString());
                pointObject.put("region", capturePoint.getCaptureRegion().toJSON());
                pointArray.put(pointObject);
            }

            jsonObject.put("capturepoints", pointArray);

            try {
                FileWriter writer = new FileWriter(file);
                writer.write(jsonObject.toString(4));
                writer.flush();
                writer.close();
            } catch(IOException ex) {
                System.out.println("Error while saving capture points file!");
                ex.printStackTrace();
            }
        } catch(JSONException ex) {
            System.out.println("Error while writing all capture points to file!");
            ex.printStackTrace();
        }
    }

    private List<CapturePointDefinition> parseDefinitions(File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException ex) {
                System.out.println("Error while creating file: " + file.getName() + "!");
                ex.printStackTrace();
            }
        }
        List<CapturePointDefinition> definitions = new ArrayList<>();
        try {
            String fileData = FileUtils.readFileToString(file);
            JSONObject jsonObject = null;
            if((!fileData.isEmpty()) && (!(fileData.length() == 0))) {
                jsonObject = new JSONObject(fileData);
            } else {
                return null;
            }

            JSONArray pointArray = jsonObject.getJSONArray("capturepoints");

            for(int i = 0; i < pointArray.length(); i++) {
                JSONObject point = pointArray.getJSONObject(i);
                definitions.add(parseCapturePointDefinition(point));
            }
        } catch(JSONException | IOException ex) {
            System.out.println("Error while reading from JSON file while loading capture points!");
            ex.printStackTrace();
        }
        return definitions;
    }

    /**
     * Method used to parse a capture point definition from a json object
     * @param jsonObject the JSON object to parse from
     * @return the capture point definition, if any, that was parsed
     */
    public CapturePointDefinition parseCapturePointDefinition(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            long timeToCapture = jsonObject.getLong("captureTime");
            boolean scaleTime = (jsonObject.has("scale-time") ? jsonObject.getBoolean("scale-time") : false);
            CapturePointDefinition.CaptureCondition captureCondition =
            ParsingUtils.getEnumFromString(CapturePointDefinition.CaptureCondition.class,
                    jsonObject.getString("capture-rule"),
                    CapturePointDefinition.CaptureCondition.MAJORITY);
            Region region = RegionParser.parseCuboidRegion(jsonObject.getJSONObject("region").getJSONObject("bounds"));

            return new CapturePointDefinition(name, region, timeToCapture, scaleTime, captureCondition);
        } catch(JSONException ex) {
            System.out.println("Error reading JSON from file while loading capture point!");
            ex.printStackTrace();
        }
        return null;
    }
}
