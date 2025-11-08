package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.common.feature.teleport.TeleportRequest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportRequestManager {
    
    private final Map<UUID, TeleportRequest> requests = new ConcurrentHashMap<>();
    private static final long REQUEST_TIMEOUT = 60000; // 60 seconds
    
    public void addRequest(UUID requester, UUID target, boolean tpHere) {
        long expireTime = System.currentTimeMillis() + REQUEST_TIMEOUT;
        requests.put(target, new TeleportRequest(requester, target, expireTime, tpHere));
    }
    
    @Nullable
    public TeleportRequest getRequest(UUID player) {
        TeleportRequest request = requests.get(player);
        if (request != null && request.isExpired()) {
            requests.remove(player);
            return null;
        }
        return request;
    }
    
    public void removeRequest(UUID player) {
        requests.remove(player);
    }
    
    public void clearExpired() {
        requests.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}

