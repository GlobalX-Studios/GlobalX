package it.omnisys.plugin.commands.staffchat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.chat.Message;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.managers.chat.StaffChatManager;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Date;

public class StaffChatCommand extends Command implements AddedCommand {
    public StaffChatCommand() {
        super("staffchat", Permission.GLOBALX_STAFFCHAT_USE.getPermission(), "sc");
    }

    @Override
    public String getUsage() {
        return "/sc <message>";
    }

    @Override
    public String getDescription() {
        return "Sends a message in the StaffChat channel";
    }

    @Override
    public Command getCommand() {
        return this;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!PermissionManager.hasPermission(sender, Permission.GLOBALX_STAFFCHAT_USE)) {
            ChatUtils.sendMessage(sender, GlobalX.getMessagesConfig().getString("NoPermsMSG"));
            return;
        }

        if(args.length == 0) {
            ChatUtils.sendMessage(sender, GlobalX.getMessagesConfig().getString("InsuffArgsMessage"));
            return;
        }

        Group group = new GroupManager().getPlayerGroup(sender);
        String message;
        if(sender instanceof ProxiedPlayer && group != null) {
            message = GlobalX.getMainConfig().getString("Groups." + group.getGroupName() + ".StaffChatFormat")
                    .replaceAll("%serverNameFormat%", GlobalX.getMessagesConfig().getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName()))
                    .replaceAll("%player_prefix%", group.getPrefix())
                    .replaceAll("%player_name%", sender.getName())
                    .replaceAll("%message%", String.join(" ", args));
        } else {
            message = GlobalX.getMessagesConfig().getString("StaffChatFormat")
                    .replaceAll("%serverNameFormat%", GlobalX.getMessagesConfig().getString("ServerNameFormat").replace("%serverName%", GlobalX.getMessagesConfig().getString("ConsoleServer")))
                    .replaceAll("%player_name%", GlobalX.getMessagesConfig().getString("ConsoleNameFormat"))
                    .replaceAll("%message%", String.join(" ", args));;
        }

        StaffChatManager.getStaffChat().broadcastMessage(message);
        StaffChatManager.getStaffChat().getMessages().add(new Message(String.join(" ", args), sender, new Date(), null));
    }
}
