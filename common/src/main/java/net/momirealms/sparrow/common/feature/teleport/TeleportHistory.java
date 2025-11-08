package net.momirealms.sparrow.common.feature.teleport;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeleportHistory {
    
    private final int maxHistory;
    private final List<TeleportLocation> history;
    
    public TeleportHistory(int maxHistory) {
        this.maxHistory = maxHistory;
        this.history = new ArrayList<>();
    }
    
    public void addLocation(@NotNull TeleportLocation location) {
        // 添加到列表开头
        history.add(0, location);
        
        // 如果超过最大数量，移除最旧的
        if (history.size() > maxHistory) {
            history.remove(history.size() - 1);
        }
    }
    
    @NotNull
    public List<TeleportLocation> getHistory() {
        return new ArrayList<>(history);
    }
    
    public TeleportLocation getLocation(int index) {
        if (index < 0 || index >= history.size()) {
            return null;
        }
        return history.get(index);
    }
    
    public int size() {
        return history.size();
    }
    
    public void clear() {
        history.clear();
    }
    
    public void removeLocation(int index) {
        if (index >= 0 && index < history.size()) {
            history.remove(index);
        }
    }
    
    public void removeExpired(long maxAge) {
        history.removeIf(loc -> loc.isExpired(maxAge));
    }
}

