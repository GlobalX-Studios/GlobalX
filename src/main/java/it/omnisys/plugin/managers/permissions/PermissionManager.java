package it.omnisys.plugin.managers.permissions;

import it.omnisys.plugin.GlobalX;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

public class PermissionManager {
    public static boolean hasPermission(CommandSender sender, Permission permission) {
        return sender.hasPermission(permission.getPermission());
    }

    public static HashMap<CommandSender, Boolean> checkAllPlayers(Permission permission) {
        HashMap<CommandSender, Boolean> checkedPlayers = new HashMap<>();
        GlobalX.getPlugin().getProxy().getScheduler().runAsync(GlobalX.getPlugin(), () -> {
            for(ProxiedPlayer p : GlobalX.getPlugin().getProxy().getPlayers()) checkedPlayers.put(p, p.hasPermission(permission.getPermission()));
        });

        return checkedPlayers;
    }
}
