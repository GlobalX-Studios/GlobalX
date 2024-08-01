package it.omnisys.plugin.commands.globalchat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.GroupManager;
import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class GlobalCommand extends Command implements AddedCommand {
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private final ServerGroupManager serverGroupManager = new ServerGroupManager();
    private final GroupManager groupManager = new GroupManager();

    public GlobalCommand() {
        super("global", PermissionManager.GLOBALX_GLOBALCHAT_USE, "gc", "globalchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionManager.GLOBALX_GLOBALCHAT_USE)) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
            return;
        }

        String message = String.join(" ", args);
        if (message.isEmpty()) {
            new GlobalToggleCommand().execute(sender, args);
            return;
        }

        ServerGroup serverGroup = serverGroupManager.getServerGroupByName(args[0]);
        if (serverGroup != null) {
            ChatUtils.handleServerGroupMessage(sender, args, serverGroup, groupManager);
        } else {
            ChatUtils.handleGlobalMessage(sender, message, groupManager);
        }
    }

    @Override
    public String getUsage() {
        return "/global [serverGroup] <message>";
    }

    @Override
    public String getDescription() {
        return "Broadcasts a message";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}