package net.momirealms.sparrow.bukkit.feature.teleport;

import me.earthme.luminol.api.entity.EntityTeleportAsyncEvent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportHistory;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * 监听玩家传送事件，记录传送历史
 */
public class TeleportHistoryListener implements Listener {
    
    private final SparrowBukkitPlugin plugin;
    private final java.util.Set<java.util.UUID> skipNextTeleport = java.util.concurrent.ConcurrentHashMap.newKeySet();
    
    public TeleportHistoryListener(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 标记下一次传送不记录（用于 back 传送）
     */
    public void skipNextTeleport(java.util.UUID playerUUID) {
        skipNextTeleport.add(playerUUID);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        
        // 检查是否有跳过标记（来自 back GUI）
        if (player.hasMetadata("sparrow_skip_back_record")) {
            player.removeMetadata("sparrow_skip_back_record", plugin.getLoader());
            return;
        }
        
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to == null) {
            return;
        }
        
        // 排除某些传送原因
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.SPECTATE || 
            cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            return;
        }
        
        // 从配置读取最小记录距离
        var config = plugin.getConfigManager().loadConfig("config.yml");
        double minDistance = config.getDouble("back-system.min-distance", 10.0);
        
        // 同世界传送：检查距离
        if (from.getWorld().equals(to.getWorld())) {
            double distance = from.distance(to);
            if (distance < minDistance) {
                // 距离太近，不记录
                return;
            }
        }
        // 跨世界传送：总是记录
        
        // 记录传送前的位置（包括 spawn, tpa, home, warp 等所有传送）
        TeleportHistory history = plugin.getTeleportHistoryManager().getHistory(player.getUniqueId());
        history.addLocation(TeleportUtils.fromBukkitLocation(from));
    }
    
    // Folia/Luminol 异步传送事件（Folia 专用）
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTeleportAsync(EntityTeleportAsyncEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        Location from = event.getEntity().getLocation(); // 当前位置（传送前）
        Location to = event.getDestination(); // 目标位置
        
        // 检查目标位置是否在历史记录中（如果是，说明是从 back 传送的，不记录）
        TeleportHistory history = plugin.getTeleportHistoryManager().getHistory(player.getUniqueId());
        for (TeleportLocation loc : history.getHistory()) {
            Location histLoc = TeleportUtils.toBukkitLocation(loc);
            if (histLoc != null && to.getWorld().equals(histLoc.getWorld())) {
                double dist = to.distance(histLoc);
                if (dist < 1.0) { // 目标位置在历史记录中，说明是从 back 传送的
                    return;
                }
            }
        }
        
        // 从配置读取最小记录距离
        var config = plugin.getConfigManager().loadConfig("config.yml");
        double minDistance = config.getDouble("back-system.min-distance", 10.0);
        
        // 同世界传送：检查距离
        if (from.getWorld().equals(to.getWorld())) {
            double distance = from.distance(to);
            if (distance < minDistance) {
                return;
            }
        }
        
        // 记录传送前的位置
        history.addLocation(TeleportUtils.fromBukkitLocation(from));
    }
    
    // 记录死亡位置
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getPlayer().getLocation();
        
        // 记录死亡位置到传送历史
        TeleportHistory history = plugin.getTeleportHistoryManager().getHistory(event.getPlayer().getUniqueId());
        history.addLocation(TeleportUtils.fromBukkitLocation(deathLocation));
    }
}

