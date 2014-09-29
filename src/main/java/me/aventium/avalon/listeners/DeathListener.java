package me.aventium.avalon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getEntity().getLocation().getWorld().strikeLightning(event.getEntity().getLocation());
        if(event.getEntity().getKiller() != null) event.setDeathMessage("§c" + event.getEntity().getName() + " §6slain by §c" + event.getEntity().getKiller().getName());
        else event.setDeathMessage(null);
    }

}
