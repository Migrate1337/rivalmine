package org.migrate1337.rivalmine.gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.migrate1337.rivalmine.RivalMine;

import java.util.ArrayList;
import java.util.List;

public class MineMenu {
    private final RivalMine plugin;
    private int currentPage = 0;
    private final int itemsPerPage = 26;
    private final List<ItemStack> mineItems = new ArrayList<>();
    private Inventory menu;

    public MineMenu(RivalMine plugin) {
        this.plugin = plugin;
        loadMines();
        updateMenu();
    }

    private void loadMines() {
        ConfigurationSection minesSection = plugin.getConfig().getConfigurationSection("mines");
        if (minesSection != null) {
            for (String mineName : minesSection.getKeys(false)) {
                ConfigurationSection mineSection = minesSection.getConfigurationSection(mineName);
                String world = mineSection.getString("world", "unknown");
                Location firstPoint = new Location(
                        plugin.getServer().getWorld(world),
                        mineSection.getInt("firstPoint.x"),
                        mineSection.getInt("firstPoint.y"),
                        mineSection.getInt("firstPoint.z")
                );
                Location secondPoint = new Location(
                        plugin.getServer().getWorld(world),
                        mineSection.getInt("secondPoint.x"),
                        mineSection.getInt("secondPoint.y"),
                        mineSection.getInt("secondPoint.z")
                );

                mineItems.add(createMineItem(mineName, firstPoint, secondPoint));
            }
        }
    }

    private void updateMenu() {

        menu = Bukkit.createInventory(null, 27, "Page " + getCurrentPage());
        menu.clear();

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, mineItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            menu.setItem(i - startIndex, mineItems.get(i));
        }

        if (endIndex < mineItems.size()) {
            menu.setItem(26, createNextPageItem());
        }

        if (currentPage > 0) {
            menu.setItem(18, createPreviousPageItem());
        }
    }

    private ItemStack createMineItem(String mineName, Location firstPoint, Location secondPoint) {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b" + mineName);
            meta.setLore(List.of(
                    "§aFirst point: " + formatLocation(firstPoint),
                    "§aSecond point: " + formatLocation(secondPoint)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNextPageItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aNext page");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPreviousPageItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cPrevious page");
            item.setItemMeta(meta);
        }
        return item;
    }

    private String formatLocation(Location location) {
        return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
    }

    public void openMenu(Player player) {
        updateMenu();
        player.openInventory(menu);
    }

    public Inventory getMenu() {
        return menu;
    }

    public void nextPage(Player player) {
        if ((currentPage + 1) * itemsPerPage < mineItems.size()) {
            currentPage++;
            updateMenu();
            player.openInventory(menu);
        }
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            updateMenu();
            player.openInventory(menu);
        }
    }

    public int getCurrentPage() {
        return currentPage + 1;
    }
}
