package org.migrate1337.rivalmine.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.mines.AutoMine;
import org.migrate1337.rivalmine.utils.MineManager;
import org.migrate1337.rivalmine.utils.MineType;

public class CreateAutoMine implements CommandExecutor {
    private final RivalMine plugin;
    private final Map<Player, Location[]> selections; // Stores selected points for each player

    public CreateAutoMine(RivalMine plugin) {
        this.plugin = plugin;
        this.selections = new HashMap<>();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available to players.");
            return true;
        } else {
            Player player = (Player) sender;
            if (!player.hasPermission("rivalmine.*")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            } else if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Please specify the name of the mine.");
                return true;
            } else {
                String mineName = args[0];
                if (this.plugin.getConfig().getConfigurationSection("mines." + mineName) != null) {
                    player.sendMessage(ChatColor.RED + "A mine with the name '" + mineName + "' already exists!");
                    return true;
                } else {
                    Location[] points = this.plugin.getSelections().get(player);
                    if (points == null) {
                        player.sendMessage(ChatColor.RED + "Error: points not found.");
                        return true;
                    } else if (points.length >= 2 && points[0] != null && points[1] != null) {
                        Location firstPoint = points[0];
                        Location secondPoint = points[1];
                        ConfigurationSection mineSection = this.plugin.getConfig().createSection("mines." + mineName);
                        AutoMine autoMine = new AutoMine(mineName, firstPoint, secondPoint, this.plugin);
                        this.saveMineData(mineName, firstPoint, secondPoint); // Save mine data
                        MineManager mineManager = this.plugin.getMineManager();
                        Map<String, MineType> mineTypes = mineManager.loadMineTypes(mineName);

                        try {
                            autoMine.updateCurrentMineType(mineTypes); // Update current mine type
                        } catch (IllegalStateException var15) {
                            player.sendMessage(ChatColor.RED + "No available mine types to select.");
                            return true;
                        }

                        player.sendMessage(ChatColor.GREEN + "Auto mine '" + mineName + "' created successfully!");
                        return true;
                    } else {
                        player.sendMessage("First, select two points using the item.");
                        return true;
                    }
                }
            }
        }
    }

    private void saveMineData(String name, Location firstPoint, Location secondPoint) {
        String worldName = firstPoint.getWorld().getName();
        ConfigurationSection mines = this.plugin.getConfig().getConfigurationSection("mines");
        if (mines == null) {
            mines = this.plugin.getConfig().createSection("mines");
        }

        ConfigurationSection mineSection = mines.createSection(name);
        mineSection.set("world", worldName);
        mineSection.set("firstPoint.x", firstPoint.getBlockX());
        mineSection.set("firstPoint.y", firstPoint.getBlockY());
        mineSection.set("firstPoint.z", firstPoint.getBlockZ());
        mineSection.set("secondPoint.x", secondPoint.getBlockX());
        mineSection.set("secondPoint.y", secondPoint.getBlockY());
        mineSection.set("secondPoint.z", secondPoint.getBlockZ());
        mineSection.set("cooldown", 30); // Set cooldown time
        mineSection.set("currentTypeID", "default"); // Set current mine type
        ConfigurationSection typesSection = mineSection.createSection("mineTypes");
        this.plugin.getLogger().info("Creating mine: " + name + ". Mine types added.");

        // Adding mine types with their chances
        ConfigurationSection defaultSection = typesSection.createSection("default");
        defaultSection.set("displayName", "Normal Mine");
        defaultSection.set("spawnChance", 85);
        ConfigurationSection defaultBlocks = defaultSection.createSection("blockChances");
        defaultBlocks.set("STONE", 50);
        defaultBlocks.set("COAL_ORE", 30);
        defaultBlocks.set("IRON_ORE", 15);
        defaultBlocks.set("GOLD_ORE", 5);

        ConfigurationSection epicSection = typesSection.createSection("epic");
        epicSection.set("displayName", "Epic Mine");
        epicSection.set("spawnChance", 15);
        ConfigurationSection epicBlocks = epicSection.createSection("blockChances");
        epicBlocks.set("STONE", 50);
        epicBlocks.set("COAL_ORE", 30);
        epicBlocks.set("IRON_ORE", 10);
        epicBlocks.set("GOLD_ORE", 7);
        epicBlocks.set("DIAMOND_ORE", 3);

        this.plugin.saveConfig(); // Save configuration
    }
}
