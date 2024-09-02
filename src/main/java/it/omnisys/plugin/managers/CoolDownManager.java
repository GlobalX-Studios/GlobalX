package it.omnisys.plugin.managers;

import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.omnisys.plugin.GlobalX;

public class CoolDownManager {

    private static final List<ProxiedPlayer> cooldown = new ArrayList<>();

    public static boolean isPlayerInCoolDown(ProxiedPlayer p) {
        return cooldown.contains(p);
    }

    public static void putPlayerInCoolDown(CommandSender s) {
        if (!(s instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) s;

        if (PermissionManager.hasPermission(s, Permission.GLOBALX_GLOBALCHAT_COOLDOWN_BYPASS)) return;
        if (!cooldown.contains(s)) {
            cooldown.add(p);

            Group g = new GroupManager().getPlayerGroup(p);
            GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> cooldown.remove(s), g.getCooldown().longValue(), TimeUnit.SECONDS);
        }
    }
}
