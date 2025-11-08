package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;

public class TpHereAdminCommand extends BukkitCommandFeature<CommandSender> {

    public TpHereAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "tphere_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("player", PlayerParser.playerParser())
                .handler(commandContext -> {
                    Player sender = commandContext.sender();
                    Player target = commandContext.get("player");
                    
                    if (sender.equals(target)) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TPA_SELF);
                        return;
                    }
                    
                    // Add tphere request
                    SparrowBukkitPlugin.getInstance().getTeleportRequestManager().addRequest(sender.getUniqueId(), target.getUniqueId(), true);
                    
                    // Notify sender
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_TPHERE_SENT, Component.text(target.getName()));
                    
                    // Notify target
                    Component receivedMsg = TranslationManager.render(MessageConstants.COMMANDS_TELEPORT_TPHERE_RECEIVED.arguments(Component.text(sender.getName())).build());
                    Component buttonsMsg = TranslationManager.render(MessageConstants.COMMANDS_TELEPORT_TPA_RECEIVED_BUTTONS.build())
                            .clickEvent(ClickEvent.runCommand("/tpaccept"));
                    
                    target.sendMessage(receivedMsg);
                    target.sendMessage(buttonsMsg);
                });
    }
}

