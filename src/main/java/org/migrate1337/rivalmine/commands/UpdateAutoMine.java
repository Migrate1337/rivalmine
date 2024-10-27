package org.migrate1337.rivalmine.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.utils.MineManager;
import org.migrate1337.rivalmine.utils.MineType;
import org.migrate1337.rivalmine.utils.PlayerTeleportManager;

public class UpdateAutoMine implements CommandExecutor {
    private final RivalMine plugin;
    private final MineManager mineManager;
    private final Map<String, BukkitRunnable> activeTimers = new HashMap<>();
    private final Map<String, Long> nextUpdateTimes = new HashMap<>();

    public UpdateAutoMine(RivalMine plugin) {
        this.plugin = plugin;
        this.mineManager = new MineManager(plugin);
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
                player.sendMessage(ChatColor.RED + "Please specify the mine name.");
                return true;
            } else {
                String mineName = args[0];
                ConfigurationSection mineSection = this.plugin.getConfig().getConfigurationSection("mines." + mineName);
                if (mineSection == null) {
                    player.sendMessage(ChatColor.RED + "No mine found with the name '" + mineName + "'.");
                    return true;
                } else {
                    String worldName = mineSection.getString("world");
                    World world = this.plugin.getServer().getWorld(worldName);
                    if (world == null) {
                        player.sendMessage(ChatColor.RED + "World '" + worldName + "' not found.");
                        return true;
                    } else {
                        Location firstPoint = new Location(world, mineSection.getInt("firstPoint.x"), mineSection.getInt("firstPoint.y"), mineSection.getInt("firstPoint.z"));
                        Location secondPoint = new Location(world, mineSection.getInt("secondPoint.x"), mineSection.getInt("secondPoint.y"), mineSection.getInt("secondPoint.z"));

                        MineType selectedMineType = null;
                        if (args.length > 1) {
                            String typeName = args[1];
                            Map<String, MineType> mineTypes = this.mineManager.loadMineTypes(mineName);
                            selectedMineType = mineTypes.get(typeName);

                            if (selectedMineType == null) {
                                player.sendMessage(ChatColor.RED + "Mine type '" + typeName + "' not found for mine '" + mineName + "'.");
                                return true;
                            }
                        } else {
                            selectedMineType = this.mineManager.selectMineType(this.mineManager.loadMineTypes(mineName));
                        }

                        this.updateMine(firstPoint, secondPoint, mineSection, player, selectedMineType);
                        this.startAutoUpdate(mineName, firstPoint, secondPoint, mineSection, player);
                        return true;
                    }
                }
            }
        }
    }

    public void updateMine(Location firstPoint, Location secondPoint, ConfigurationSection mineSection, Player player, MineType selectedMineType) {
        PlayerTeleportManager teleportManager = new PlayerTeleportManager(this.mineManager.getAutoMine(mineSection.getName()));
        teleportManager.teleportPlayersInMine();
        World world = firstPoint.getWorld();
        int minX = Math.min(firstPoint.getBlockX(), secondPoint.getBlockX());
        int maxX = Math.max(firstPoint.getBlockX(), secondPoint.getBlockX());
        int minY = Math.min(firstPoint.getBlockY(), secondPoint.getBlockY());
        int maxY = Math.max(firstPoint.getBlockY(), secondPoint.getBlockY());
        int minZ = Math.min(firstPoint.getBlockZ(), secondPoint.getBlockZ());
        int maxZ = Math.max(firstPoint.getBlockZ(), secondPoint.getBlockZ());

        if (selectedMineType == null) {
            String nextTypeName = mineSection.getString("nextTypeID");
            Map<String, MineType> mineTypes = this.mineManager.loadMineTypes(mineSection.getName());
            selectedMineType = mineTypes.get(nextTypeName);

            if (selectedMineType == null) {
                this.plugin.getLogger().warning("Could not find next mine type for: " + mineSection.getName() + ". Using a random type.");
                selectedMineType = this.mineManager.selectMineType(mineTypes);
            }
        }

        Map<Material, Integer> selectedBlocks = selectedMineType.getBlockChances();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(this.mineManager.getRandomBlock(selectedBlocks));
                }
            }
        }

        this.saveCurrentMineType(mineSection, selectedMineType.getName(), selectedMineType.getDisplayName());

        MineType nextMineType = this.mineManager.selectMineType(this.mineManager.loadMineTypes(mineSection.getName()));
        if (nextMineType != null) {
            mineSection.set("nextTypeID", nextMineType.getName());
            mineSection.set("nextTypeDisplayName", nextMineType.getDisplayName());
            this.plugin.saveConfig();
        } else {
            this.plugin.getLogger().warning("Could not select the next mine type for: " + mineSection.getName());
        }

        long cooldownSeconds = (long) mineSection.getInt("cooldown", 30);
        long nextUpdateTime = System.currentTimeMillis() + cooldownSeconds * 1000L;
        this.nextUpdateTimes.put(mineSection.getName(), nextUpdateTime);
    }

    public void startAutoUpdate(String mineName, final Location firstPoint, final Location secondPoint, final ConfigurationSection mineSection, final Player player) {
        if (!this.activeTimers.containsKey(mineName)) {
            BukkitRunnable task = new BukkitRunnable() {
                public void run() {
                    updateMine(firstPoint, secondPoint, mineSection, player, null);
                }
            };
            task.runTaskTimer(this.plugin, 0L, (long) (20 * mineSection.getInt("cooldown", 30)));
            this.activeTimers.put(mineName, task);

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Auto-update for mine '" + mineName + "' has started.");
            }
        }
    }

    private void saveCurrentMineType(ConfigurationSection mineSection, String currentTypeID, String currentTypeDisplayName) {
        mineSection.set("currentTypeID", currentTypeID);
        mineSection.set("currentType", currentTypeDisplayName);
        this.plugin.saveConfig();
    }

    public Long getNextUpdateTime(String mineName) {
        return this.nextUpdateTimes.get(mineName);
    }
}
