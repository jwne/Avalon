package me.aventium.avalon.editing.editors;

import me.aventium.avalon.editing.Editor;
import me.aventium.avalon.editing.event.PlayerSelectPointEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class RegionEditor extends Editor {

    private Vector pos1, pos2;

    @Override
    public String name() {
        return "Region";
    }

    @Override
    public Material toolMaterial() {
        return Material.DIAMOND_HOE;
    }

    @Override
    public RegionEditor clone() {
        return new RegionEditor();
    }

    public Vector getPos1() { return pos1; }

    public Vector getPos2() { return pos2; }

    public void setPosition1(Vector position) {
        this.pos1 = position;
    }

    public void setPosition2(Vector position) {
        this.pos2 = position;
    }

    @EventHandler
    public void onSelect(PlayerSelectPointEvent event) {
        if (event.getSession().getEditor() instanceof RegionEditor) {
            RegionEditor editor = (RegionEditor) event.getSession().getEditor();
            if (event.getSelectionType().equals(PlayerSelectPointEvent.SelectionType.LEFT)) {
                editor.setPosition1(event.getPointSelected());
                event.getSession().getUser().sendMessage("§9Position 1 set.");
            } else if (event.getSelectionType().equals(PlayerSelectPointEvent.SelectionType.RIGHT)) {
                editor.setPosition2(event.getPointSelected());
                event.getSession().getUser().sendMessage("§9Position 2 set.");
            }
        }
    }


}
