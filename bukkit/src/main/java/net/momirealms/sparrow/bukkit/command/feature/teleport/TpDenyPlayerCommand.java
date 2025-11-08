package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.feature.teleport.TeleportRequest;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class TpDenyPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public TpDenyPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "tpdeny_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    var requestManager = SparrowBukkitPlugin.getInstance().getTeleportRequestManager();
                    
                    TeleportRequest request = requestManager.getRequest(player.getUniqueId());
                    if (request == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TPACCEPT_NO_REQUEST);
                        return;
                    }
                    
                    Player requester = Bukkit.getPlayer(request.getRequester());
                    
                    // Remove request
                    requestManager.removeRequest(player.getUniqueId());
                    
                    // Notify both players
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TPDENY_SUCCESS);
                    
                    if (requester != null && requester.isOnline()) {
                        Component notifyMsg = TranslationManager.render(MessageConstants.COMMANDS_TELEPORT_TPDENY_NOTIFY.arguments(Component.text(player.getName())).build());
                        requester.sendMessage(notifyMsg);
                    }
                });
    }
}

