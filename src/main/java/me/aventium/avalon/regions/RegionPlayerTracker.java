package me.aventium.avalon.regions;

import com.google.common.collect.Sets;
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

public class RegionPlayerTracker implements Listener {

    protected final HCRegion region;
    protected final Set<UUID> playersOnPoint = Sets.newHashSet();

    public RegionPlayerTracker(HCRegion region) {
        this.region = region;
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
        if(!player.getWorld().equals(region.getRegion().getBounds().getWorld()))
            if(!player.isDead() && region.getRegion().contains(to.toBlockVector()) && !playersOnPoint.contains(player.getUniqueId())) {
                player.sendMessage("§6Entering §l" + region.getName().replaceAll("_", " ") + "§6");
                playersOnPoint.add(player.getUniqueId());
            } else if(!region.getRegion().contains(to.toBlockVector()) && playersOnPoint.contains(player.getUniqueId())){
                player.sendMessage("§6Leaving §l" + region.getName().replaceAll("_", " ") + "§6");
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
