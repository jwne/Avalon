package me.aventium.avalon.editing.event;

import me.aventium.avalon.editing.EditSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class PlayerSelectPointEvent extends Event {

    public static enum SelectionType {
        LEFT,
        RIGHT;

        public static SelectionType fromAction(Action action) {
            for(SelectionType type : values()) {
                if(type.name().equalsIgnoreCase(action.name().replaceAll("_", "").replace("CLICK", "").replaceAll("BLOCK", "").replace("AIR", "")))
                    return type;
            }
            return null;
        }
    }

    private static final HandlerList handlers = new HandlerList();

    private EditSession session;
    private Vector pointSelected;
    private SelectionType type;

    public PlayerSelectPointEvent(EditSession session, Vector point, Action action) {
        this.session = session;
        this.pointSelected = point;
        this.type = SelectionType.fromAction(action);
    }

    public EditSession getSession() {
        return session;
    }

    public Vector getPointSelected() {
        return pointSelected.clone();
    }

    public SelectionType getSelectionType() {
        return type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
