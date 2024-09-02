package it.omnisys.plugin.commands.globalchat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.chat.GlobalChatManager;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class GlobalCommand extends Command implements AddedCommand {
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private final ServerGroupManager serverGroupManager = new ServerGroupManager();
    private final GroupManager groupManager = new GroupManager();
    private final Chat globalChat = GlobalChatManager.getGlobalChat();

    public GlobalCommand() {
        super("global", Permission.GLOBALX_GLOBALCHAT_USE.getPermission(), "gc", "globalchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!PermissionManager.hasPermission(sender, Permission.GLOBALX_GLOBALCHAT_USE)) {
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
            GlobalChatManager.handleServerGroupMessage(sender, args, serverGroup, groupManager);
        } else {
            GlobalChatManager.handleGlobalMessage(sender, message, groupManager);
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