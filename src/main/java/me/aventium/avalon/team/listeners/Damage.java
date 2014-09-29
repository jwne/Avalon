package me.aventium.avalon.team.listeners;

import me.aventium.avalon.Avalon;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Damage implements Listener {

    private static List<UUID> cannotAttack = new ArrayList<>();

    public static void addPlayer(final UUID uuid) {
        cannotAttack.add(uuid);
        Avalon.get().getServer().getScheduler().runTaskLater(Avalon.get(), new Runnable() {
            @Override
            public void run() {
                cannotAttack.remove(uuid);
            }
        }, 200L);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(cannotAttack.contains(player)) event.setCancelled(true);
            if(event.getEntity() instanceof Player) {
                Player da = (Player) event.getEntity();
                if(Avalon.get().getTeamManager().getPlayerTeam(player) != null && Avalon.get().getTeamManager().getPlayerTeam(da) != null && Avalon.get().getTeamManager().getPlayerTeam(player).equals(Avalon.get().getTeamManager().getPlayerTeam(da)) && !Avalon.get().getTeamManager().getPlayerTeam(player).friendlyFireOn()) {
                    event.setCancelled(true);
                }
            }
        } else if(event.getDamager() instanceof Arrow) {

            Arrow a = (Arrow) event.getDamager();
            if(a.getShooter() instanceof Player) {
                Player player = (Player) a.getShooter();
                if(event.getEntity() instanceof Player) {
                    Player da = (Player) event.getEntity();
                    if(Avalon.get().getTeamManager().getPlayerTeam(player) != null && Avalon.get().getTeamManager().getPlayerTeam(da) != null && Avalon.get().getTeamManager().getPlayerTeam(player).equals(Avalon.get().getTeamManager().getPlayerTeam(da)) && !Avalon.get().getTeamManager().getPlayerTeam(player).friendlyFireOn()) {
                        event.setCancelled(true);
                    }
                }
            }

        } else if(event.getDamager() instanceof Snowball) {
            Snowball s = (Snowball) event.getDamager();
            if(s.getShooter() instanceof Player) {
                Player player = (Player) s.getShooter();
                if(event.getEntity() instanceof Player) {
                    Player da = (Player) event.getEntity();
                    if(Avalon.get().getTeamManager().getPlayerTeam(player) != null && Avalon.get().getTeamManager().getPlayerTeam(da) != null && Avalon.get().getTeamManager().getPlayerTeam(player).equals(Avalon.get().getTeamManager().getPlayerTeam(da)) && !Avalon.get().getTeamManager().getPlayerTeam(player).friendlyFireOn()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
