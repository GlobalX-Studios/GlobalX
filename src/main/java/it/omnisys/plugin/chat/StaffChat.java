package it.omnisys.plugin.chat;

import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.checkerframework.checker.optional.qual.OptionalBottom;

public class StaffChat extends Chat {
    public StaffChat() {
        super(Permission.GLOBALX_STAFFCHAT_USE.getPermission());
    }

    @Override
    public void broadcastMessage(CommandSender s, TextComponent broadcast, @OptionalBottom ServerGroup serverGroup) {}
}
