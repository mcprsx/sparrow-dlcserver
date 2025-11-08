package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.feature.teleport.TeleportUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class SetHomePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public SetHomePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "sethome_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("name", StringParser.stringParser())
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    String homeName = commandContext.getOrDefault("name", "home");
                    
                    // Get max homes from permissions
                    int maxHomes = getMaxHomes(player);
                    int currentHomes = SparrowBukkitPlugin.getInstance().getHomeManager().getHomeCount(player.getUniqueId());
                    
                    // If trying to set a new home (not overwriting existing one)
                    if (currentHomes >= maxHomes && SparrowBukkitPlugin.getInstance().getHomeManager().getHome(player.getUniqueId(), homeName) == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_HOME_LIMIT, Component.text(maxHomes));
                        return;
                    }
                    
                    SparrowBukkitPlugin.getInstance().getHomeManager().setHome(
                            player.getUniqueId(),
                            homeName,
                            TeleportUtils.fromBukkitLocation(player.getLocation())
                    );
                    
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SETHOME_SUCCESS, Component.text(homeName));
                });
    }
    
    /**
     * Get max homes based on permissions
     * Checks permissions like sparrow.home.1, sparrow.home.2, sparrow.home.5, etc.
     * Returns the highest number found, or 1 if no permission found
     */
    private int getMaxHomes(Player player) {
        // Check for unlimited homes permission
        if (player.hasPermission("sparrow.home.unlimited")) {
            return Integer.MAX_VALUE;
        }
        
        int maxHomes = 1; // Default: 1 home
        
        // Check for numeric permissions from 1 to 100
        for (int i = 1; i <= 100; i++) {
            if (player.hasPermission("sparrow.home." + i)) {
                maxHomes = Math.max(maxHomes, i);
            }
        }
        
        return maxHomes;
    }
}



