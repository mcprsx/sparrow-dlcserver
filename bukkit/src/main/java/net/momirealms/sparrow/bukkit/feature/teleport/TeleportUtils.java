package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportUtils {

    @NotNull
    public static TeleportLocation fromBukkitLocation(@NotNull Location location) {
        return new TeleportLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    @Nullable
    public static Location toBukkitLocation(@NotNull TeleportLocation teleportLocation) {
        World world = Bukkit.getWorld(teleportLocation.getWorld());
        if (world == null) {
            return null;
        }
        return new Location(
                world,
                teleportLocation.getX(),
                teleportLocation.getY(),
                teleportLocation.getZ(),
                teleportLocation.getYaw(),
                teleportLocation.getPitch()
        );
    }

    public static boolean teleport(@NotNull Player player, @NotNull TeleportLocation teleportLocation) {
        Location location = toBukkitLocation(teleportLocation);
        if (location == null) {
            return false;
        }
        return player.teleport(location);
    }
}

