package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Set;

public class WarpsPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public WarpsPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "warps_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .handler(commandContext -> {
                    Set<String> warps = SparrowBukkitPlugin.getInstance().getWarpManager().getWarpNames();
                    
                    if (warps.isEmpty()) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARPS_EMPTY);
                        return;
                    }
                    
                    Component warpsList = Component.text(String.join(", ", warps));
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARPS_LIST, warpsList);
                });
    }
}

