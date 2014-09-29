package me.aventium.avalon.editing;

import me.aventium.avalon.Avalon;
import me.aventium.avalon.editing.event.PlayerSelectPointEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class CreationTool implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        Editor editor = Editors.getEditor(event.getItem().getType());
        if(editor == null) return;
        if(!event.getPlayer().hasPermission("editor." + editor.name())) return;

        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            PlayerSelectPointEvent.SelectionType type = PlayerSelectPointEvent.SelectionType.fromAction(event.getAction());
            Vector point = event.getClickedBlock().getLocation().toVector();
            if(EditSessions.getSession(event.getPlayer(), editor) == null) {
                event.setCancelled(true);
                EditSessions.createNewSession(event.getPlayer(), editor.clone());
                event.getPlayer().sendMessage("§6§lChanged your editor to " + editor.name() + ".");
            }
            event.setCancelled(true);
            Avalon.get().callEvent(new PlayerSelectPointEvent(EditSessions.getSession(event.getPlayer(), editor), point, event.getAction()));
        }
    }

}
