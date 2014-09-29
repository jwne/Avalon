package me.aventium.avalon.editing;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.editing.editors.KOTHEditor;
import me.aventium.avalon.editing.editors.RegionEditor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Editors {

    private static List<Editor> editors = new ArrayList<>();

    public static void loadEditors() {
        Avalon.get().registerEvents(new CreationTool());
        addEditor(new KOTHEditor());
        addEditor(new RegionEditor());
    }

    private static void addEditor(Editor editor) {
        Avalon.get().registerEvents(editor);
        editors.add(editor);
    }

    public static Editor getEditor(Material tool) {
        for(Editor editor : editors) {
            if(editor.toolMaterial().equals(tool)) return editor;
        }
        return null;
    }


}
