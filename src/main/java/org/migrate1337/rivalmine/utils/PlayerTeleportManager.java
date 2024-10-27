package org.migrate1337.rivalmine.utils;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.migrate1337.rivalmine.mines.AutoMine;

public class PlayerTeleportManager {
    private final AutoMine autoMine;

    public PlayerTeleportManager(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public void teleportPlayersInMine() {
        Location topLocation = this.getTopLocation();
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while(var2.hasNext()) {
            Player player = (Player)var2.next();
            if (this.isInMine(player)) {
                double x = player.getLocation().getX();
                double z = player.getLocation().getZ();
                player.teleport(new Location(topLocation.getWorld(), x, topLocation.getY(), z));
            }
        }

    }

    private boolean isInMine(Player player) {
        Location playerLocation = player.getLocation();
        return playerLocation.getWorld().equals(this.autoMine.getFirstPoint().getWorld()) && playerLocation.getBlockX() >= Math.min(this.autoMine.getFirstPoint().getBlockX(), this.autoMine.getSecondPoint().getBlockX()) && playerLocation.getBlockX() <= Math.max(this.autoMine.getFirstPoint().getBlockX(), this.autoMine.getSecondPoint().getBlockX()) && playerLocation.getBlockY() >= Math.min(this.autoMine.getFirstPoint().getBlockY(), this.autoMine.getSecondPoint().getBlockY()) && playerLocation.getBlockY() <= Math.max(this.autoMine.getFirstPoint().getBlockY(), this.autoMine.getSecondPoint().getBlockY()) && playerLocation.getBlockZ() >= Math.min(this.autoMine.getFirstPoint().getBlockZ(), this.autoMine.getSecondPoint().getBlockZ()) && playerLocation.getBlockZ() <= Math.max(this.autoMine.getFirstPoint().getBlockZ(), this.autoMine.getSecondPoint().getBlockZ());
    }

    private Location getTopLocation() {
        int topY = Math.max(this.autoMine.getFirstPoint().getBlockY(), this.autoMine.getSecondPoint().getBlockY()) + 1;
        Location topLocation = this.autoMine.getFirstPoint().clone();
        topLocation.setY((double)topY);
        return topLocation;
    }
}
