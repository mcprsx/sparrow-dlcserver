package net.momirealms.sparrow.common.feature.teleport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportLocation {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final long timestamp; // 记录时间戳

    public TeleportLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch, long timestamp) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timestamp = timestamp;
    }

    public TeleportLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch) {
        this(world, x, y, z, yaw, pitch, System.currentTimeMillis());
    }

    public TeleportLocation(@NotNull String world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f, System.currentTimeMillis());
    }

    @NotNull
    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(long maxAge) {
        return System.currentTimeMillis() - timestamp > maxAge;
    }

    @NotNull
    public static TeleportLocation fromString(@NotNull String data) {
        String[] parts = data.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid location data: " + data);
        }
        String world = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = parts.length > 4 ? Float.parseFloat(parts[4]) : 0f;
        float pitch = parts.length > 5 ? Float.parseFloat(parts[5]) : 0f;
        long timestamp = parts.length > 6 ? Long.parseLong(parts[6]) : System.currentTimeMillis();
        return new TeleportLocation(world, x, y, z, yaw, pitch, timestamp);
    }

    @NotNull
    public String toString() {
        return world + "," + x + "," + y + "," + z + "," + yaw + "," + pitch + "," + timestamp;
    }
}

