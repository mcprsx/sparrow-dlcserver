package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.feature.teleport.TeleportUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class SetSpawnAdminCommand extends BukkitCommandFeature<CommandSender> {

    public SetSpawnAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "setspawn_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    SparrowBukkitPlugin.getInstance().getSpawnManager().setSpawn(
                            TeleportUtils.fromBukkitLocation(player.getLocation())
                    );
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SETSPAWN_SUCCESS);
                });
    }
}



