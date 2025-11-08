package net.momirealms.sparrow.bukkit.command.feature.world;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

public class TimeAdminCommand extends BukkitCommandFeature<CommandSender> {

    public TimeAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "time_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        // /sparrow time set <time> [world]
        manager.command(builder
                .literal("set")
                .required("time", StringParser.stringParser())
                .optional("world", WorldParser.worldParser())
                .handler(commandContext -> {
                    String timeStr = commandContext.get("time");
                    World world = commandContext.getOrDefault("world", null);
                    
                    // If no world specified and sender is player, use their world
                    if (world == null) {
                        if (commandContext.sender() instanceof Player player) {
                            world = player.getWorld();
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_NO_WORLD);
                            return;
                        }
                    }
                    
                    long time = parseTime(timeStr);
                    if (time == -1) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_INVALID);
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_USAGE);
                        return;
                    }
                    
                    world.setTime(time);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_SET, 
                            Component.text(world.getName()), Component.text(time));
                }));

        // /sparrow time add <amount> [world]
        manager.command(builder
                .literal("add")
                .required("amount", IntegerParser.integerParser())
                .optional("world", WorldParser.worldParser())
                .handler(commandContext -> {
                    int amount = commandContext.get("amount");
                    World world = commandContext.getOrDefault("world", null);
                    
                    if (world == null) {
                        if (commandContext.sender() instanceof Player player) {
                            world = player.getWorld();
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_NO_WORLD);
                            return;
                        }
                    }
                    
                    world.setTime(world.getTime() + amount);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_ADD, 
                            Component.text(amount), Component.text(world.getName()));
                }));

        // /sparrow time query [world]
        manager.command(builder
                .literal("query")
                .optional("world", WorldParser.worldParser())
                .handler(commandContext -> {
                    World world = commandContext.getOrDefault("world", null);
                    
                    if (world == null) {
                        if (commandContext.sender() instanceof Player player) {
                            world = player.getWorld();
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_NO_WORLD);
                            return;
                        }
                    }
                    
                    long time = world.getTime();
                    String timeOfDay = getTimeOfDay(time);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_QUERY, 
                            Component.text(world.getName()), Component.text(time), Component.text(timeOfDay));
                }));

        return builder.handler(commandContext -> {
            handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_HELP);
            handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TIME_USAGE);
        });
    }
    
    /**
     * Parse time string to ticks
     * Supports: day, night, noon, midnight, sunrise, sunset, or numeric value
     */
    private long parseTime(String timeStr) {
        return switch (timeStr.toLowerCase()) {
            case "day", "sunrise" -> 0L;
            case "noon" -> 6000L;
            case "sunset" -> 12000L;
            case "night", "midnight" -> 18000L;
            default -> {
                try {
                    long time = Long.parseLong(timeStr);
                    if (time >= 0 && time <= 24000) {
                        yield time;
                    }
                } catch (NumberFormatException ignored) {
                }
                yield -1L;
            }
        };
    }
    
    /**
     * Get time of day description
     */
    private String getTimeOfDay(long time) {
        time = time % 24000;
        if (time < 450 || time >= 23000) return "午夜";
        if (time < 6000) return "日出";
        if (time < 8000) return "早晨";
        if (time < 12000) return "中午";
        if (time < 13000) return "下午";
        if (time < 18000) return "日落";
        return "夜晚";
    }
}

