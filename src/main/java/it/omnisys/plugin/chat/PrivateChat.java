package it.omnisys.plugin.chat;

import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.permissions.Permission;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.checkerframework.checker.optional.qual.OptionalBottom;

public class PrivateChat extends Chat{
    public PrivateChat() {
        super(Permission.GLOBALX_PRIVATEMSG_SEND.getPermission());
    }

    @Override
    public void broadcastMessage(CommandSender s, TextComponent broadcast, @OptionalBottom ServerGroup serverGroup) {}
}
