package org.migrate1337.rivalmine.PAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.migrate1337.rivalmine.RivalMine;
import org.migrate1337.rivalmine.commands.UpdateAutoMine;

public class MinePlaceholder extends PlaceholderExpansion {
    private final RivalMine plugin;
    private final UpdateAutoMine updateAutoMine;

    public MinePlaceholder(RivalMine plugin, UpdateAutoMine updateAutoMine) {
        this.plugin = plugin;
        this.updateAutoMine = updateAutoMine;
    }

    public String getIdentifier() {
        return "rivalmine";
    }

    public String getAuthor() {
        return "Migrate1337";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onRequest(OfflinePlayer player, String param) {
        if (player == null) {
            return null;
        } else {
            String mineName;
            String nextTypeID;
            if (param.toLowerCase().startsWith("current_mine_type_")) {
                mineName = param.substring("current_mine_type_".length());
                nextTypeID = this.plugin.getConfig().getString("mines." + mineName + ".currentType", "default");
                return nextTypeID;
            } else if (param.toLowerCase().startsWith("next_mine_type_")) {
                mineName = param.substring("next_mine_type_".length());
                nextTypeID = this.plugin.getConfig().getString("mines." + mineName + ".nextTypeDisplayName", "Next type did not find.");
                return nextTypeID;
            } else if (param.toLowerCase().startsWith("remaining_time_")) {
                mineName = param.substring("remaining_time_".length());
                Long nextUpdateTime = this.updateAutoMine.getNextUpdateTime(mineName);
                if (nextUpdateTime != null) {
                    long remainingTimeMillis = nextUpdateTime - System.currentTimeMillis();
                    long remainingTime = Math.max(0L, remainingTimeMillis / 1000L);
                    return String.valueOf(remainingTime);
                } else {
                    return "Null";
                }
            } else {
                return null;
            }
        }
    }
}
