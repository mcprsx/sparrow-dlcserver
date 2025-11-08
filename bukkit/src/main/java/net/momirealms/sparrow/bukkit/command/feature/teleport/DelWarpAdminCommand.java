package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class DelWarpAdminCommand extends BukkitCommandFeature<CommandSender> {

    public DelWarpAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "delwarp_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("name", StringParser.stringParser())
                .handler(commandContext -> {
                    String warpName = commandContext.get("name");
                    
                    if (SparrowBukkitPlugin.getInstance().getWarpManager().getWarp(warpName) == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARP_NOT_FOUND, Component.text(warpName));
                        return;
                    }
                    
                    SparrowBukkitPlugin.getInstance().getWarpManager().deleteWarp(warpName);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_DELWARP_SUCCESS, Component.text(warpName));
                });
    }
}

