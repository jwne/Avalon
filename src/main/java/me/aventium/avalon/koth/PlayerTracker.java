package me.aventium.avalon.koth;

import com.google.common.collect.Sets;
import me.aventium.avalon.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class PlayerTracker implements Listener {

    protected final Region captureRegion;
    protected final Set<UUID> playersOnPoint = Sets.newHashSet();

    public PlayerTracker(Region captureRegion) {
        this.captureRegion = captureRegion;
    }

    public Set<UUID> getPlayersOnPoint() {
        return this.playersOnPoint;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        handleMove(event.getPlayer(), event.getTo().toVector());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        handleMove(event.getPlayer(), event.getTo().toVector());
    }

    private void handleMove(Player player, Vector to) {
        if(!player.getWorld().equals(captureRegion.getBounds().getWorld()))
        if(!player.isDead() && captureRegion.contains(to.toBlockVector())) {
            playersOnPoint.add(player.getUniqueId());
        } else {
            playersOnPoint.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        playersOnPoint.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        playersOnPoint.remove(event.getEntity().getUniqueId());
    }

}
