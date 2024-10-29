package org.migrate1337.rivalmine.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.migrate1337.rivalmine.RivalMine;

public class MineMenuListener implements Listener {
    private final RivalMine plugin;
    private final MineMenu mineMenu;

    public MineMenuListener(RivalMine plugin) {
        this.plugin = plugin;
        this.mineMenu = plugin.getMineMenu();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clicked = event.getInventory();
        if (clicked.equals(mineMenu.getMenu())) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§aNext page")) {
                    if (event.getWhoClicked() instanceof Player) {
                        mineMenu.nextPage((Player) event.getWhoClicked());
                    }
                } else if (displayName.equals("§cPrevious page")) {
                    if (event.getWhoClicked() instanceof Player) {
                        mineMenu.previousPage((Player) event.getWhoClicked());
                    }
                }
            }
        }
    }
}
