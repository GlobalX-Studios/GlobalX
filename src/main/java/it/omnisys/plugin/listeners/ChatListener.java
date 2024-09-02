package it.omnisys.plugin.listeners;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.CoolDownManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.managers.chat.GlobalChatManager;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatListener implements Listener {

    private static final Configuration mainConfig = GlobalX.getMainConfig();
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final HashMap<CommandSender, @OptionalBottom ServerGroup> globalToggledPlayers = GlobalChatManager.getGlobalChat().getSenderServerGroup();
    private static final Chat globalChat = GlobalChatManager.getGlobalChat();

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (e.getMessage().startsWith("/")) return;

        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            Group g = getPlayerGroup(p, e);
            if (g == null) return;

            if (globalToggledPlayers.containsKey(p)) {
                handleGlobalToggledPlayer(p, g, e);
                return;
            }

            if (!mainConfig.getBoolean("GlobalChatPrefix.Enable")) {
                handleGlobalChatPrefixDisabled(p, e);
                return;
            }

            handlePrefixMessage(p, g, e);
        }
    }

    private Group getPlayerGroup(ProxiedPlayer p, ChatEvent e) {
        Group g = new GroupManager().getPlayerGroup(p);
        if (g == null) {
            ChatUtils.sendMessage(p, messagesConfig.getString("NoPermsMSG"));
            e.setCancelled(true);
        }
        return g;
    }

    private void handleGlobalToggledPlayer(ProxiedPlayer p, Group g, ChatEvent e) {
        if (!PermissionManager.hasPermission(p, Permission.GLOBALX_GLOBALCHAT_USE)) {
            ChatUtils.sendMessage(p, messagesConfig.getString("NoPermsMSG"));
            e.setCancelled(true);
            return;
        }

        TextComponent broadcast = new TextComponent(GlobalChatManager.formatGlobalPlaceholders(p, e.getMessage(), g));

        globalChat.broadcastMessage(p, broadcast, globalToggledPlayers.get(p));
        e.setCancelled(true);
    }

    private void handleGlobalChatPrefixDisabled(ProxiedPlayer p, ChatEvent e) {
        ChatUtils.sendMessage(p, messagesConfig.getString("PrefixChattingDisabled"));
        e.setCancelled(true);
    }

    private void handlePrefixMessage(ProxiedPlayer p, Group g, ChatEvent e) {
        List<String> prefixes = g.getPrefixes();

        for (String prfx : prefixes) {
            if (!e.getMessage().startsWith(prfx)) break;
            String message = e.getMessage().replace(prfx, "");
            List<String> newWords = new ArrayList<>(Arrays.asList(message.split(" ")));
            ServerGroup serverGroup = new ServerGroupManager().getServerGroupByName(newWords.get(0));
            if (serverGroup != null) newWords.remove(0);

            e.setCancelled(true);

            if (message.isEmpty()) {
                ChatUtils.sendMessage(p, messagesConfig.getString("OnlyPrefixMSG"));
                return;
            }

            if (!PermissionManager.hasPermission(p, Permission.GLOBALX_GLOBALCHAT_USE)) {
                ChatUtils.sendMessage(p, messagesConfig.getString("NoPermsMSG"));
                return;
            }

            if (CoolDownManager.isPlayerInCoolDown(p)) {
                ChatUtils.sendMessage(p, messagesConfig.getString("CantUseInCoolDown"));
                return;
            }

            TextComponent broadcast = new TextComponent(GlobalChatManager.formatGlobalPlaceholders(p, String.join(" ", newWords), g));

            globalChat.broadcastMessage(p, broadcast, serverGroup);
            e.setCancelled(true);
        }
    }
}