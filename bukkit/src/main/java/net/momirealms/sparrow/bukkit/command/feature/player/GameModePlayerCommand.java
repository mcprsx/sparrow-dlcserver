package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

public class GameModePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public GameModePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "gamemode_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("mode", IntegerParser.integerParser(0, 3))
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    int modeId = commandContext.get("mode");
                    GameMode mode = getGameModeFromId(modeId);
                    
                    if (player.getGameMode() == mode) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_GAMEMODE_FAILED_SAME, 
                            Component.text(getGameModeName(mode)));
                        return;
                    }
                    
                    player.setGameMode(mode);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_GAMEMODE_SUCCESS, 
                        Component.text(getGameModeName(mode)));
                });
    }
    
    private GameMode getGameModeFromId(int id) {
        return switch (id) {
            case 0 -> GameMode.SURVIVAL;
            case 1 -> GameMode.CREATIVE;
            case 2 -> GameMode.ADVENTURE;
            case 3 -> GameMode.SPECTATOR;
            default -> GameMode.SURVIVAL;
        };
    }
    
    private String getGameModeName(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "生存模式";
            case CREATIVE -> "創造模式";
            case ADVENTURE -> "冒險模式";
            case SPECTATOR -> "旁觀模式";
        };
    }
}

