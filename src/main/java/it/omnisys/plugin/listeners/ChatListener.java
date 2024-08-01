package it.omnisys.plugin.listeners;

import it.omnisys.plugin.commands.globalchat.GlobalToggleCommand;
import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.CoolDownManager;
import it.omnisys.plugin.managers.GroupManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import it.omnisys.plugin.GlobalX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.omnisys.plugin.managers.PermissionManager.GLOBALX_GLOBALCHAT_USE;

public class ChatListener implements Listener {

    private static final Configuration mainConfig = GlobalX.getMainConfig();
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (e.getMessage().startsWith("/")) return;

        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            Group g = new GroupManager().getPlayerGroup(p);
            if (g == null) {
                ChatUtils.sendMessage(p, messagesConfig.getString("NoPermsMSG"));
                e.setCancelled(true);
                return;
            }

            if (GlobalToggleCommand.globalToggledPlayers.containsKey(p)) {
                TextComponent broadcast = new TextComponent(ChatUtils.formatGlobalPlaceholders(p, e.getMessage(), g));

                ChatUtils.broadcastMessage(p, broadcast, GlobalToggleCommand.globalToggledPlayers.get(p));
                e.setCancelled(true);
                return;
            }

            if (!mainConfig.getBoolean("GlobalChatPrefix.Enable")) {
                ChatUtils.sendMessage(p, messagesConfig.getString("PrefixChattingDisabled"));
                e.setCancelled(true);
                return;
            }

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

                if (!p.hasPermission(GLOBALX_GLOBALCHAT_USE)) {
                    ChatUtils.sendMessage(p, messagesConfig.getString("NoPermsMSG"));
                    return;
                }

                if (CoolDownManager.isPlayerInCoolDown(p)) {
                    ChatUtils.sendMessage(p, messagesConfig.getString("CantUseInCoolDown"));
                    return;
                }

                TextComponent broadcast = new TextComponent(ChatUtils.formatGlobalPlaceholders(p, String.join(" ", newWords), g));

                ChatUtils.broadcastMessage(p, broadcast, serverGroup);
                e.setCancelled(true);
                return;
            }
        }
    }
}
