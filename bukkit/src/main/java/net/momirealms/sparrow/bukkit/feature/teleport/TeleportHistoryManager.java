package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportHistory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportHistoryManager {
    
    private final SparrowBukkitPlugin plugin;
    private final Map<UUID, TeleportHistory> playerHistories = new ConcurrentHashMap<>();
    
    public TeleportHistoryManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }
    
    @NotNull
    public TeleportHistory getHistory(@NotNull UUID player) {
        return playerHistories.computeIfAbsent(player, k -> {
            int maxHistory = getMaxHistoryForPlayer(player);
            return new TeleportHistory(maxHistory);
        });
    }
    
    /**
     * Get max history based on permissions
     * Checks permissions like sparrow.back.8, sparrow.back.16, sparrow.back.24, etc.
     * Returns the highest number found, or default from config
     */
    private int getMaxHistoryForPlayer(@NotNull UUID playerUUID) {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerUUID);
        if (player == null) {
            var config = plugin.getConfigManager().loadConfig("config.yml");
            return config.getInt("back-system.default-max-history", 8);
        }
        
        // Check for unlimited history permission
        if (player.hasPermission("sparrow.back.unlimited")) {
            return Integer.MAX_VALUE;
        }
        
        var config = plugin.getConfigManager().loadConfig("config.yml");
        int maxHistory = config.getInt("back-system.default-max-history", 8);
        
        // Check for numeric permissions from 1 to 100
        for (int i = 1; i <= 100; i++) {
            if (player.hasPermission("sparrow.back." + i)) {
                maxHistory = Math.max(maxHistory, i);
            }
        }
        
        return maxHistory;
    }
    
    public void removeHistory(@NotNull UUID player) {
        playerHistories.remove(player);
    }
    
    public void clearAll() {
        playerHistories.clear();
    }
}

