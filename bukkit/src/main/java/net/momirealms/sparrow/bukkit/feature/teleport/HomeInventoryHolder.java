package net.momirealms.sparrow.bukkit.feature.teleport;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HomeInventoryHolder implements InventoryHolder {
    
    private final UUID playerUUID;
    private final int page;
    
    public HomeInventoryHolder(UUID playerUUID, int page) {
        this.playerUUID = playerUUID;
        this.page = page;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public int getPage() {
        return page;
    }
    
    @Override
    @NotNull
    public Inventory getInventory() {
        return null; // Not used
    }
}

