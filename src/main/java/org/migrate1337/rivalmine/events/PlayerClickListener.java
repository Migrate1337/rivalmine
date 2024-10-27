package org.migrate1337.rivalmine.events;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.items.SelectionItem;

public class PlayerClickListener implements Listener {
    private final RivalMine plugin;
    private final Map<Player, Location[]> selections; // Stores selections for each player

    public PlayerClickListener(RivalMine plugin) {
        this.plugin = plugin;
        this.selections = new HashMap();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().isSimilar(SelectionItem.createPotatoFragment())) {
            Location secondPoint;
            Location[] points;
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                secondPoint = event.getClickedBlock().getLocation();
                points = (Location[]) this.plugin.getSelections().getOrDefault(player, new Location[2]);
                points[0] = secondPoint;
                this.plugin.getSelections().put(player, points);
                player.sendMessage("First point selected. X: " + secondPoint.getBlockX() + " Y: " + secondPoint.getBlockY() + " Z: " + secondPoint.getBlockZ());
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                secondPoint = event.getClickedBlock().getLocation();
                points = (Location[]) this.plugin.getSelections().get(player);
                if (points != null) {
                    points[1] = secondPoint;
                    this.plugin.getSelections().put(player, points);
                    player.sendMessage("Second point selected. X: " + secondPoint.getBlockX() + " Y: " + secondPoint.getBlockY() + " Z: " + secondPoint.getBlockZ());
                    player.sendMessage(ChatColor.YELLOW + "Now type the command /createautomine to create the auto mine.");
                } else {
                    player.sendMessage("First, select the first point.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().isSimilar(SelectionItem.createPotatoFragment())) {
            event.setCancelled(true);
        }
    }
}
