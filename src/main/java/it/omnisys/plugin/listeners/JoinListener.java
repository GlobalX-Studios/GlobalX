package it.omnisys.plugin.listeners;

import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import it.omnisys.plugin.utils.UpdateChecker;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import it.omnisys.plugin.GlobalX;

public class JoinListener implements Listener {
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) {
        if(e.getPlayer().hasPermission(PermissionManager.GLOBALX_ADMIN)) {
            new UpdateChecker(GlobalX.getPlugin(), 102941).getVersion(version -> {
                if (!GlobalX.getPlugin().getDescription().getVersion().equals(version)) {
                    e.getPlayer().sendMessage(new TextComponent(ChatUtils.chat(messagesConfig.getString("prefix") + " §aAn update was found! (" + version + ")")));
                }
            });
        }
    }
}
