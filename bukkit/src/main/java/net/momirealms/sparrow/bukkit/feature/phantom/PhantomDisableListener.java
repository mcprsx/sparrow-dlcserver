package net.momirealms.sparrow.bukkit.feature.phantom;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Simple listener to completely disable phantom spawning
 * Phantoms will not spawn naturally in any world
 */
public class PhantomDisableListener implements Listener {

    private final SparrowBukkitPlugin plugin;

    public PhantomDisableListener(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Cancel all phantom spawns except from spawner eggs and custom spawns
        if (event.getEntityType() == EntityType.PHANTOM) {
            CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
            
            // Allow spawner eggs and custom spawns (for other plugins)
            if (reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || 
                reason == CreatureSpawnEvent.SpawnReason.SPAWNER ||
                reason == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                return;
            }
            
            // Cancel all natural phantom spawns
            event.setCancelled(true);
        }
    }
}

