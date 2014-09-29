package me.aventium.avalon.regions;

import com.google.common.collect.ImmutableList;
import me.aventium.avalon.Avalon;
import me.aventium.avalon.editing.EditSession;
import me.aventium.avalon.editing.EditSessions;
import me.aventium.avalon.editing.Editors;
import me.aventium.avalon.editing.editors.RegionEditor;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegionModule {

    private final List<HCRegion> regions = new ArrayList<>();

    public RegionModule(ImmutableList<HCRegion> regions) {
        if(regions != null) this.regions.addAll(regions);
    }

    public List<HCRegion> getRegions() { return regions; }


    public boolean regionExists(String name) {
        for(HCRegion point : regions) {
            if(point.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void createNewPoint(String name, Player player, boolean deathban) {
        if(EditSessions.getSession(player, Editors.getEditor(Material.DIAMOND_HOE)) == null) {
            player.sendMessage("§cYou must select two points for the region with a diamond hoe!");
            return;
        }

        EditSession session = EditSessions.getSession(player, Editors.getEditor(Material.DIAMOND_HOE));
        RegionEditor editor = (RegionEditor) session.getEditor();

        Vector p1 = editor.getPos1();
        Vector p2 = editor.getPos2();

        CuboidRegion region = new CuboidRegion(p1, p2);
        region.getBounds().setWorld(player.getWorld());
        HCRegion rg = new HCRegion(name, region, deathban);
        this.regions.add(rg);
        rg.register();
        saveRegions(new File(Avalon.get().getDataFolder(), "regions.json"));
        player.sendMessage("§cRegion created!");
    }

    public void deleteRegion(String name) {
        HCRegion toRemove = null;
        for(HCRegion region : this.regions) {
            if(region.getName().equalsIgnoreCase(name)) {
                toRemove = region;
                break;
            }
        }
        this.regions.remove(toRemove);
        saveRegions(new File(Avalon.get().getDataFolder(), "regions.json"));
    }

    public HCRegion getCapturePoint(String name) {
        for(HCRegion rg : this.regions) {
            if(rg.getName().equalsIgnoreCase(name)) return rg;
        }
        return null;
    }

    /**
     * Load the module, call before CapturePointModule#enable()
     */
    public void load() {
        List<HCRegion> regions = parseRegions(new File(Avalon.get().getDataFolder(), "regions.json"));
        if(regions == null || regions.size() == 0) {
            System.out.println("No regions loaded!");
        } else {
            for(HCRegion region: regions) {
                this.regions.add(region);
            }
            System.out.println(regions.size() + " regions loaded!");
        }

        for(HCRegion rg : this.regions) {
            rg.register();
        }
    }

    /**
     * Unload the module, call after CapturePointModule#disable()
     */
    public void unload() {
        saveRegions(new File(Avalon.get().getDataFolder(), "regions.json"));
        for(HCRegion rg : this.regions) {
            rg.unregister();
        }
    }

    /**
     * Save all capture points to file
     * @param file the file to save capture points to
     */
    private void saveRegions(File file) {
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
            for(HCRegion region : this.regions) {
                JSONObject pointObject = new JSONObject();
                pointObject.put("name", region.getName());
                pointObject.put("deathban", region.isDeathban());
                pointObject.put("region", region.getRegion().toJSON());
                pointArray.put(pointObject);
            }

            jsonObject.put("regions", pointArray);

            try {
                FileWriter writer = new FileWriter(file);
                writer.write(jsonObject.toString(4));
                writer.flush();
                writer.close();
            } catch(IOException ex) {
                System.out.println("Error while saving regions file!");
                ex.printStackTrace();
            }
        } catch(JSONException ex) {
            System.out.println("Error while writing all regions to file!");
            ex.printStackTrace();
        }
    }

    private List<HCRegion> parseRegions(File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException ex) {
                System.out.println("Error while creating file: " + file.getName() + "!");
                ex.printStackTrace();
            }
        }
        List<HCRegion> regions = new ArrayList<>();
        try {
            String fileData = FileUtils.readFileToString(file);
            JSONObject jsonObject = null;
            if((!fileData.isEmpty()) && (!(fileData.length() == 0))) {
                jsonObject = new JSONObject(fileData);
            } else {
                return null;
            }

            JSONArray pointArray = jsonObject.getJSONArray("regions");

            for(int i = 0; i < pointArray.length(); i++) {
                JSONObject region = pointArray.getJSONObject(i);
                regions.add(parseRegion(region));
            }
        } catch(JSONException | IOException ex) {
            System.out.println("Error while reading from JSON file while loading capture points!");
            ex.printStackTrace();
        }
        return regions;
    }

    /**
     * Method used to parse a capture point definition from a json object
     * @param jsonObject the JSON object to parse from
     * @return the capture point definition, if any, that was parsed
     */
    public HCRegion parseRegion(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            boolean deathban = jsonObject.getBoolean("deathban");
            Region region = RegionParser.parseCuboidRegion(jsonObject.getJSONObject("region").getJSONObject("bounds"));

            return new HCRegion(name, region, deathban);
        } catch(JSONException ex) {
            System.out.println("Error reading JSON from file while loading capture point!");
            ex.printStackTrace();
        }
        return null;
    }


}
