package org.migrate1337.rivalmine.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SelectionItem implements Listener {
    private final ItemStack item;

    public SelectionItem(ItemStack item) {
        this.item = item;
    }

    public static ItemStack createPotatoFragment() {
        ItemStack item = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        // Item name and description
        meta.setDisplayName(ChatColor.YELLOW + "Point Selector");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Use to select mine points"));

        // Add enchantment effect and hide it in the description
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
