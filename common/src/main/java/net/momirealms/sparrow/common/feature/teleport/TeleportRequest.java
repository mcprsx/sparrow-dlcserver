package net.momirealms.sparrow.common.feature.teleport;

import java.util.UUID;

public class TeleportRequest {
    
    private final UUID requester;  // 发起请求的玩家
    private final UUID target;     // 目标玩家
    private final long expireTime; // 过期时间
    private final boolean tpHere;  // 是否是 tphere 请求
    
    public TeleportRequest(UUID requester, UUID target, long expireTime, boolean tpHere) {
        this.requester = requester;
        this.target = target;
        this.expireTime = expireTime;
        this.tpHere = tpHere;
    }
    
    public UUID getRequester() {
        return requester;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
    
    public boolean isTpHere() {
        return tpHere;
    }
}

