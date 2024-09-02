package it.omnisys.plugin.commands.privatemessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class BlockMessagesCommand extends Command implements AddedCommand {

    Configuration messageConfig = GlobalX.getMessagesConfig();
    @Getter
    static List<CommandSender> blockingPlayers = new ArrayList<>();

    public BlockMessagesCommand() {
        super("block", Permission.GLOBALX_PRIVATEMSG_BLOCK.getPermission(), "stopmsg", "blockmsg");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!PermissionManager.hasPermission(sender, Permission.GLOBALX_PRIVATEMSG_BLOCK)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("NoPermsMSG"));
            return;
        }
        if (blockingPlayers.contains(sender)) {
            blockingPlayers.remove(sender);
            ChatUtils.sendMessage(sender, messageConfig.getString("PrivateMessagesEnabled"));
            return;
        }

        blockingPlayers.add(sender);
        ChatUtils.sendMessage(sender, messageConfig.getString("PrivateMessagesDisabled"));
    }

    @Override
    public String getUsage() {
        return "/block";
    }

    @Override
    public String getDescription() {
        return "Block all private messages";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
