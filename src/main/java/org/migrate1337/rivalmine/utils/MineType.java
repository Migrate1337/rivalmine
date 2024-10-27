//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.migrate1337.rivalmine.utils;

import java.util.Map;
import org.bukkit.Material;

public class MineType {
    private final String name;
    private final String displayName;
    private final int spawnChance;
    private final Map<Material, Integer> blockChances;

    public MineType(String name, String displayName, int spawnChance, Map<Material, Integer> blockChances) {
        this.name = name;
        this.displayName = displayName;
        this.spawnChance = spawnChance;
        this.blockChances = blockChances;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getSpawnChance() {
        return this.spawnChance;
    }

    public Map<Material, Integer> getBlockChances() {
        return this.blockChances;
    }
}
