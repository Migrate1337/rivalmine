package org.migrate1337.rivalmine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.mines.AutoMine;

public class MineManager {
    private final RivalMine plugin;
    private final Random random = new Random();
    private final Map<String, Long> nextUpdateTimes = new HashMap();

    public MineManager(RivalMine plugin) {
        this.plugin = plugin;
    }

    public Map<String, MineType> loadMineTypes(String mineName) {
        Map<String, MineType> mineTypes = new HashMap();
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("mines." + mineName + ".mineTypes");
        if (section == null) {
            return mineTypes;
        } else {
            Iterator var4 = section.getKeys(false).iterator();

            while(true) {
                String key;
                ConfigurationSection typeSection;
                do {
                    if (!var4.hasNext()) {
                        return mineTypes;
                    }

                    key = (String)var4.next();
                    typeSection = section.getConfigurationSection(key);
                } while(typeSection == null);

                String displayName = typeSection.getString("displayName");
                int spawnChance = typeSection.getInt("spawnChance");
                Map<Material, Integer> blockChances = new HashMap();
                ConfigurationSection blockChancesSection = typeSection.getConfigurationSection("blockChances");
                if (blockChancesSection != null) {
                    Iterator var11 = blockChancesSection.getKeys(false).iterator();

                    while(var11.hasNext()) {
                        String blockKey = (String)var11.next();
                        blockChances.put(Material.valueOf(blockKey), blockChancesSection.getInt(blockKey));
                    }
                }

                mineTypes.put(key, new MineType(key, displayName, spawnChance, blockChances));
            }
        }
    }

    public List<AutoMine> loadAllMines() {
        List<AutoMine> allMines = new ArrayList();
        ConfigurationSection minesSection = this.plugin.getConfig().getConfigurationSection("mines");
        if (minesSection == null) {
            return allMines;
        } else {
            Iterator var3 = minesSection.getKeys(false).iterator();

            while(var3.hasNext()) {
                String mineName = (String)var3.next();
                ConfigurationSection mineSection = minesSection.getConfigurationSection(mineName);
                if (mineSection != null) {
                    String worldName = mineSection.getString("world");
                    Location firstPoint = new Location(this.plugin.getServer().getWorld(worldName), (double)mineSection.getInt("firstPoint.x"), (double)mineSection.getInt("firstPoint.y"), (double)mineSection.getInt("firstPoint.z"));
                    Location secondPoint = new Location(this.plugin.getServer().getWorld(worldName), (double)mineSection.getInt("secondPoint.x"), (double)mineSection.getInt("secondPoint.y"), (double)mineSection.getInt("secondPoint.z"));
                    AutoMine autoMine = new AutoMine(mineName, firstPoint, secondPoint, this.plugin);
                    allMines.add(autoMine);
                    long currentTime = System.currentTimeMillis();
                    long cooldownTime = (long)mineSection.getInt("cooldown", 30);
                    long nextUpdateTime = currentTime + cooldownTime * 1000L;
                    this.setNextUpdateTime(mineName, nextUpdateTime);
                }
            }

            return allMines;
        }
    }

    public void setNextUpdateTime(String mineName, long time) {
        this.nextUpdateTimes.put(mineName, time);
    }

    public Long getRemainingTime(String mineName) {
        Long nextUpdateTime = (Long)this.nextUpdateTimes.get(mineName);
        if (nextUpdateTime != null) {
            long remainingTime = nextUpdateTime - System.currentTimeMillis();
            return Math.max(remainingTime, 0L);
        } else {
            return null;
        }
    }

    public MineType selectMineType(Map<String, MineType> mineTypes) {
        if (mineTypes.isEmpty()) {
            throw new IllegalArgumentException("Null.");
        } else {
            int totalChance = 0;

            MineType type;
            for(Iterator var3 = mineTypes.values().iterator(); var3.hasNext(); totalChance += type.getSpawnChance()) {
                type = (MineType)var3.next();
            }

            int randomChance = this.random.nextInt(totalChance);
            int currentChance = 0;
            Iterator var5 = mineTypes.values().iterator();

            do {
                if (!var5.hasNext()) {
                    return null;
                }

                type = (MineType)var5.next();
                currentChance += type.getSpawnChance();
            } while(randomChance >= currentChance);

            return type;
        }
    }

    public Material getRandomBlock(Map<Material, Integer> blockChances) {
        int totalChance = blockChances.values().stream().mapToInt(Integer::intValue).sum();
        int randomChance = this.random.nextInt(totalChance);
        int currentChance = 0;
        Iterator var5 = blockChances.entrySet().iterator();

        Map.Entry entry;
        do {
            if (!var5.hasNext()) {
                return Material.STONE;
            }

            entry = (Map.Entry)var5.next();
            currentChance += (Integer)entry.getValue();
        } while(randomChance >= currentChance);

        return (Material)entry.getKey();
    }

    public AutoMine getAutoMine(String mineName) {
        Iterator var2 = this.loadAllMines().iterator();

        AutoMine autoMine;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            autoMine = (AutoMine)var2.next();
        } while(!autoMine.getName().equals(mineName));

        return autoMine;
    }
}
