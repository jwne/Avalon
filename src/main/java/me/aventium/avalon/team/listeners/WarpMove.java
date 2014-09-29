package me.aventium.avalon.team.listeners;

import me.aventium.avalon.Avalon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class WarpMove implements Listener {

    private UUID player;
    private int timer = 10, taskid;
    private boolean moved = false;

    public WarpMove(final Player plr, final Location location) {
        this.player = plr.getUniqueId();
        Bukkit.getServer().getPluginManager().registerEvents(this, Avalon.get());
        taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Avalon.get(), new Runnable() {
            public void run() {
                if (timer > 0) timer--;
                else {
                    if (!moved) {
                        if(Bukkit.getPlayer(player) == null) {
                            return;
                        }
                        Bukkit.getPlayer(player).teleport(location);

                        Bukkit.getPlayer(player).sendMessage(ChatColor.GRAY + "You cannot attack for 10 seconds.");
                        Damage.addPlayer(player);
                        HandlerList.unregisterAll(get());
                        cancel();
                    }
                }
            }
        }, 0, 20);
    }

    private WarpMove get() {
        return this;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (moved == true) return;
        if (e.getPlayer().getUniqueId().equals(player)) {
            Location from = e.getFrom();
            Location to = e.getTo();
            if (from.getX() != to.getX() && from.getY() != to.getY() && from.getZ() != to.getZ() && timer > 0) {
                Bukkit.getPlayer(player).sendMessage("§7You moved! Teleportation cancelled.");
                moved = true;
                cancel();
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(player.getUniqueId().equals(this.player)) {
                if(moved) return;
                this.moved = true;
            }
        }
    }

    private void cancel() {
        Bukkit.getServer().getScheduler().cancelTask(taskid);
    }

}
