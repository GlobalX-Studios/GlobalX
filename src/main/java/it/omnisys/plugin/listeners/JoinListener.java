package it.omnisys.plugin.listeners;

import it.omnisys.plugin.managers.chat.StaffChatManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import it.omnisys.plugin.utils.UpdateChecker;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import it.omnisys.plugin.GlobalX;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (PermissionManager.hasPermission(p, Permission.GLOBALX_ADMIN)) {
            new UpdateChecker(GlobalX.getPlugin(), GlobalX.getResourceId()).getVersion(version -> {
                if (!GlobalX.getPlugin().getDescription().getVersion().equals(version)) {
                    ChatUtils.sendMessage(p, "%prefix% &aThere's a new update available: &b" + version + "&7, you're currently on &a" + GlobalX.getVersion());
                }
            });
            return;
        }

        if(PermissionManager.hasPermission(p, Permission.GLOBALX_GLOBALCHAT_TOGGLE) && GlobalX.getMainConfig().getBoolean("StaffChatToggle")) {
            StaffChatManager.getStaffChat().getToggledPlayers().add(p);
            return;
        }
    }
}
