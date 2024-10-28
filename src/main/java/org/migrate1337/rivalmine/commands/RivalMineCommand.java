package org.migrate1337.rivalmine.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.migrate1337.rivalmine.RivalMine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RivalMineCommand implements CommandExecutor, TabCompleter {
    private final RivalMine plugin;

    public RivalMineCommand(RivalMine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("        §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sender.sendMessage("§eVersion: §f" + plugin.getDescription().getVersion());
            sender.sendMessage("§eAuthor: §fMigrate1337");
            sender.sendMessage("§e/rivalmine help - §fList of commands.");
            sender.sendMessage("§e/rivalmine placeholders - §fList of placeholders.");
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("                            §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sender.sendMessage("§8- §e/setpoints - §fProvides an item for point selection.");
            sender.sendMessage("§8- §e/createautomine (name) - §fCreate an auto mine.");
            sender.sendMessage("§8- §e/rivalmine delete (name) - §fDelete auto mine.");
            sender.sendMessage("§8- §e/updateautomine (name) (type) - §fUpdate auto mine (Mine type is optional).");
            sender.sendMessage("§8- §e/rivalmine list - §fList all mines.");
            sender.sendMessage("§8- §e/plugman reload RivalMine - §fReload config (Currently only through Plugman).");

        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Specify the name of the mine to remove.");
                return true;
            }
            String mineName = args[1];
            if (plugin.getConfig().getConfigurationSection("mines." + mineName) == null) {
                sender.sendMessage(ChatColor.RED + "Mine '" + mineName + "' doesn't exist.");
                return true;
            }

            plugin.getConfig().set("mines." + mineName, null);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Mine '" + mineName + "' successfully deleted.");

        } else if (args[0].equalsIgnoreCase("placeholders")) {
            sender.sendMessage("                            §x§C§F§7§7§1§5R§x§D§4§8§6§1§7i§x§D§9§9§5§1§9v§x§D§D§A§4§1§Ba§x§E§2§B§3§1§Dl§x§E§7§C§2§1§EM§x§E§C§D§1§2§0i§x§F§0§E§0§2§2n§x§F§5§E§F§2§4e                       ");
            sendPlaceholdersMessage(sender);
        } else if (args[0].equalsIgnoreCase("list")) {
            ConfigurationSection minesSection = plugin.getConfig().getConfigurationSection("mines");
            if (minesSection != null) {
                sender.sendMessage("§eMines list:");
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

                    sender.sendMessage("§e- §f" + mineName + ":");
                    sender.sendMessage("  §aFirst point: §f" + formatLocation(firstPoint));
                    sender.sendMessage("  §aSecond point: §f" + formatLocation(secondPoint));
                }
            } else {
                sender.sendMessage("§e-");
            }
        } else {
            sender.sendMessage("§cUnknown command. Use §e/rivalmine help §cfor a list of commands.");
        }
        return true;
    }

    private String formatLocation(Location location) {
        return "World: " + location.getWorld().getName() + ", X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
    }
    private void sendPlaceholdersMessage(CommandSender sender) {

        TextComponent remainingTime = createPlaceholderComponent(
                "%rivalmine_remaining_time_Mine%",
                "Click to copy",
                ChatColor.YELLOW
        );

        TextComponent remainingTimeMessage = new TextComponent("§8- ");
        remainingTimeMessage.addExtra(remainingTime);
        remainingTimeMessage.addExtra(" - §fDisplays remaining time until mine update.");

        sender.spigot().sendMessage(remainingTimeMessage);

        TextComponent currentMineType = createPlaceholderComponent(
                "%rivalmine_current_mine_type_Mine%",
                "Click to copy",
                ChatColor.YELLOW
        );
        TextComponent currentMineTypeMessage = new TextComponent("§8- ");
        currentMineTypeMessage.addExtra(currentMineType);
        currentMineTypeMessage.addExtra(" - §fDisplays the current mine type.");
        sender.spigot().sendMessage(currentMineTypeMessage);

        TextComponent nextMineType = createPlaceholderComponent(
                "%rivalmine_next_mine_type_Mine%",
                "Click to copy",
                ChatColor.YELLOW
        );
        TextComponent nextMineTypeMessage = new TextComponent("§8- ");
        nextMineTypeMessage.addExtra(nextMineType);
        nextMineTypeMessage.addExtra(" - §fDisplays the next mine type.");
        sender.spigot().sendMessage(nextMineTypeMessage);
    }

    private TextComponent createPlaceholderComponent(String placeholder, String hoverText, ChatColor color) {
        TextComponent textComponent = new TextComponent(placeholder);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, placeholder));
        textComponent.setColor(net.md_5.bungee.api.ChatColor.valueOf(color.name()));
        return textComponent;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("placeholders");
            completions.add("list");
            completions.add("delete");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            Set<String> mineNames = plugin.getConfig().getConfigurationSection("mines").getKeys(false);
            completions.addAll(mineNames);
        }

        return completions;
    }
}
