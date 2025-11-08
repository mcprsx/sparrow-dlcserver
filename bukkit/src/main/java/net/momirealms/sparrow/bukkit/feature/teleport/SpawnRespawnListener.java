package net.momirealms.sparrow.bukkit.feature.teleport;

import me.earthme.luminol.api.entity.player.PostPlayerRespawnEvent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnRespawnListener implements Listener {

    private final SparrowBukkitPlugin plugin;

    public SpawnRespawnListener(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    // Handle standard Bukkit/Paper respawn event (for compatibility)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Only handle respawn if player doesn't have a bed spawn
        if (event.isBedSpawn() || event.isAnchorSpawn()) {
            return;
        }

        TeleportLocation spawn = plugin.getSpawnManager().getSpawn();
        if (spawn != null) {
            Location location = TeleportUtils.toBukkitLocation(spawn);
            if (location != null) {
                event.setRespawnLocation(location);
            }
        }
    }

    // Handle Luminol's special PostPlayerRespawnEvent (for lightingluminol)
    // This event fires AFTER the player has respawned, allowing for reliable teleportation on Folia
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPostPlayerRespawn(PostPlayerRespawnEvent event) {
        // PostPlayerRespawnEvent is called after respawn, so we need to check if spawn is set
        TeleportLocation spawn = plugin.getSpawnManager().getSpawn();
        if (spawn != null) {
            Location location = TeleportUtils.toBukkitLocation(spawn);
            if (location != null) {
                // Only teleport if player is not at a bed/anchor spawn location already
                Location playerLoc = event.getPlayer().getLocation();
                if (playerLoc.getWorld().equals(location.getWorld())) {
                    double distance = playerLoc.distance(location);
                    // If player is already near spawn (within 5 blocks), don't teleport (they used bed/anchor)
                    if (distance > 5.0) {
                        // Use Folia-safe async teleportation
                        event.getPlayer().teleportAsync(location);
                    }
                } else {
                    // Different world, teleport anyway
                    event.getPlayer().teleportAsync(location);
                }
            }
        }
    }
}

