package org.migrate1337.rivalmine.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.migrate1337.rivalmine.RivalMine;

public class RivalMineCommand implements CommandExecutor, TabCompleter {
    private final RivalMine plugin;

    public RivalMineCommand(RivalMine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Plugin information
            sender.sendMessage("        §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sender.sendMessage("§eVersion: §f" + plugin.getDescription().getVersion());
            sender.sendMessage("§eAuthor: §fMigrate1337");
            sender.sendMessage("§e/rivalmine help - §fList of commands.");
            sender.sendMessage("§e/rivalmine placeholders - §fList of placeholders.");
        } else if (args[0].equalsIgnoreCase("help")) {
            // Command list
            sender.sendMessage("                            §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sender.sendMessage("§8- §e/setpoints - §fProvides an item for point selection.");
            sender.sendMessage("§8- §e/createautomine (name) - §fCreate an auto mine.");
            sender.sendMessage("§8- §e/updateautomine (name) (type) - §fUpdate auto mine (Mine type is optional).");
            sender.sendMessage("§8- §e/plugman reload RivalMine - §fReload config (Currently only through Plugman).");
        } else if (args[0].equalsIgnoreCase("placeholders")) {
            // Placeholder list
            sender.sendMessage("                            §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sender.sendMessage("§8- §e%rivalmine_remaining_time_Mine% - §fDisplays remaining time until mine update.");
            sender.sendMessage("§8- §e%rivalmine_current_mine_type_Mine% - §fDisplays the current mine type.");
            sender.sendMessage("§8- §e%rivalmine_next_mine_type_Mine% - §fDisplays the next mine type.");
        } else {
            sender.sendMessage("§cUnknown command. Use §e/rivalmine help §cfor a list of commands.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("placeholders");
        }

        return completions;
    }
}
