package me.aventium.avalon.editing;

import org.bukkit.Material;
import org.bukkit.event.Listener;

public abstract class Editor implements Listener, Cloneable {

    public abstract String name();

    public abstract Material toolMaterial();

    @Override
    public abstract Editor clone();

}
