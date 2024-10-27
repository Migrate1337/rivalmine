package org.migrate1337.rivalmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.items.SelectionItem;

public class SetPoints implements CommandExecutor {
    private final RivalMine plugin;

    public SetPoints(RivalMine plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("rivalmine.*")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            } else {
                ItemStack item = SelectionItem.createPotatoFragment();
                player.getInventory().addItem(new ItemStack[]{item});
                player.sendMessage("You have received an item for selecting points!");
                return true;
            }
        } else {
            return false;
        }
    }
}
