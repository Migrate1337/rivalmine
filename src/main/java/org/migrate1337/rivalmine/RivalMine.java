package org.migrate1337.rivalmine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.migrate1337.rivalmine.PAPI.MinePlaceholder;
import org.migrate1337.rivalmine.commands.*;
import org.migrate1337.rivalmine.events.PlayerClickListener;
import org.migrate1337.rivalmine.gui.MineMenu;
import org.migrate1337.rivalmine.gui.MineMenuListener;
import org.migrate1337.rivalmine.mines.AutoMine;
import org.migrate1337.rivalmine.utils.MineManager;
import org.migrate1337.rivalmine.utils.MineType;

public class RivalMine extends JavaPlugin {
    private MineManager mineManager;
    private MineMenu mineMenu;
    private final Map<Player, Location[]> selections = new HashMap<>();
    private final List<AutoMine> allAutoMines = new ArrayList<>();

    private UpdateAutoMine updateAutoMine;

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.mineMenu = new MineMenu(this);
        this.mineManager = new MineManager(this);
        this.getLogger().info("Initializing RivalMine...");
        this.loadMinesFromConfig();
        this.updateAutoMine = new UpdateAutoMine(this);
        MinePlaceholder minePlaceholder = new MinePlaceholder(this, this.updateAutoMine);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            minePlaceholder.register();
        }
        this.getLogger().info("Registering commands...");
        getServer().getPluginManager().registerEvents(new MineMenuListener(this), this);
        this.getCommand("rivalmine").setExecutor(new RivalMineCommand(this));
        this.getCommand("rivalmine").setTabCompleter(new RivalMineCommand(this));
        this.getCommand("createautomine").setExecutor(new CreateAutoMine(this));
        this.getCommand("updateautomine").setExecutor(this.updateAutoMine);
        this.getCommand("setpoints").setExecutor(new SetPoints(this));
        getCommand("updateautomine").setTabCompleter(new UpdateAutoMineTabCompleter(this));

        this.getLogger().info("Commands registered.");
        Bukkit.getPluginManager().registerEvents(new PlayerClickListener(this), this);
        this.getLogger().info("Events registered.");
        this.getLogger().info("RivalMine successfully loaded.");

        // Load mines and start auto-update for all of them
        List<AutoMine> allAutoMines = this.mineManager.loadAllMines();
        this.allAutoMines.addAll(allAutoMines);

        for (AutoMine mine : this.allAutoMines) {
            String mineName = mine.getName();
            Location firstPoint = mine.getFirstPoint();
            Location secondPoint = mine.getSecondPoint();
            ConfigurationSection mineSection = this.getConfig().getConfigurationSection("mines." + mineName);

            if (mineSection != null) {
                MineType selectedMineType = this.mineManager.selectMineType(this.mineManager.loadMineTypes(mineName));
                this.updateAutoMine.startAutoUpdate(mineName, firstPoint, secondPoint, mineSection, null);
                this.getLogger().info("Auto-update for mine '" + mineName + "' started.");
            }
        }
    }

    private void loadMinesFromConfig() {
        ConfigurationSection minesSection = this.getConfig().getConfigurationSection("mines");
        if (minesSection != null) {
            for (String mineName : minesSection.getKeys(false)) {
                this.mineManager.loadMineTypes(mineName);
            }
        } else {
            this.getLogger().warning("The 'mines' section is missing in the configuration.");
        }
    }
    public Map<Player, Location[]> getSelections() {
        return this.selections;
    }

    public UpdateAutoMine getUpdateAutoMine() {
        return this.updateAutoMine;
    }

    public MineManager getMineManager() {
        return this.mineManager;
    }

    public MineMenu getMineMenu() {
        return this.mineMenu;
    }

    public void onDisable() {
        this.getLogger().info("Disabling RivalMine...");
    }
}
