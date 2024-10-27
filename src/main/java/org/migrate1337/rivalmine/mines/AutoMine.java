//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.migrate1337.rivalmine.mines;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.utils.MineManager;
import org.migrate1337.rivalmine.utils.MineType;

public class AutoMine {
    private final Location firstPoint;
    private final Location secondPoint;
    private final RivalMine plugin;
    private final String name;
    private final MineManager mineManager;
    private MineType currentMineType;
    private final Set<Player> playersInMine = new HashSet<>();

    public AutoMine(String mineName, Location firstPoint, Location secondPoint, RivalMine plugin) {
        this.name = mineName;
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.plugin = plugin;
        this.mineManager = plugin.getMineManager();
        this.updateCurrentMineType(this.mineManager.loadMineTypes(mineName));
    }

    public void updateCurrentMineType(Map<String, MineType> mineTypes) {
        if (mineTypes.isEmpty()) {
            this.plugin.getLogger().warning("No available mine types for updating " + this.name);
        } else {
            this.currentMineType = this.mineManager.selectMineType(mineTypes);
            if (this.currentMineType == null) {
                this.plugin.getLogger().warning("Failed to update mine type for " + this.name);
            }
        }
    }

    public void createMine(ConfigurationSection mineSection) {
        World world = this.firstPoint.getWorld();
        int minX = Math.min(this.firstPoint.getBlockX(), this.secondPoint.getBlockX());
        int maxX = Math.max(this.firstPoint.getBlockX(), this.secondPoint.getBlockX());
        int minY = Math.min(this.firstPoint.getBlockY(), this.secondPoint.getBlockY());
        int maxY = Math.max(this.firstPoint.getBlockY(), this.secondPoint.getBlockY());
        int minZ = Math.min(this.firstPoint.getBlockZ(), this.secondPoint.getBlockZ());
        int maxZ = Math.max(this.firstPoint.getBlockZ(), this.secondPoint.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(this.getRandomBlockFromConfig(mineSection));
                }
            }
        }
    }

    public MineType getCurrentMineType() {
        return this.currentMineType;
    }

    public String getName() {
        return this.name;
    }

    public void addPlayer(Player player) {
        this.playersInMine.add(player);
    }

    public void removePlayer(Player player) {
        this.playersInMine.remove(player);
    }

    public Set<Player> getPlayersInMine() {
        return this.playersInMine;
    }

    private Material getRandomBlockFromConfig(ConfigurationSection mineSection) {
        if (this.currentMineType == null) {
            this.plugin.getLogger().warning("Current mine type is null");
            return Material.STONE;
        } else {
            return this.mineManager.getRandomBlock(this.currentMineType.getBlockChances());
        }
    }

    public Location getFirstPoint() {
        return this.firstPoint;
    }

    public Location getSecondPoint() {
        return this.secondPoint;
    }
}
