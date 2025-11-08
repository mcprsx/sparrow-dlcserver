package net.momirealms.sparrow.bukkit.feature.teleport;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class BackInventoryHolder implements InventoryHolder {
    
    @Override
    @NotNull
    public Inventory getInventory() {
        return null; // Not used
    }
}

