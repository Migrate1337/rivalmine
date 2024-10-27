package org.migrate1337.rivalmine.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.utils.MineManager;
import org.migrate1337.rivalmine.utils.MineType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateAutoMineTabCompleter implements TabCompleter {
    private final RivalMine plugin;
    private final MineManager mineManager;

    public UpdateAutoMineTabCompleter(RivalMine plugin) {
        this.plugin = plugin;
        this.mineManager = plugin.getMineManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            ConfigurationSection minesSection = plugin.getConfig().getConfigurationSection("mines");
            if (minesSection != null) {
                suggestions.addAll(minesSection.getKeys(false));
            }
        } else if (args.length == 2) {
            String mineName = args[0];
            Map<String, MineType> mineTypes = mineManager.loadMineTypes(mineName);
            if (mineTypes != null) {
                suggestions.addAll(mineTypes.keySet());
            }
        }

        return suggestions;
    }
}
