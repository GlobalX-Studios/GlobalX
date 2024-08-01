package it.omnisys.plugin.managers;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.group.ServerGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerGroupManager {
    @Getter
    static List<ServerGroup> serverGroups = new ArrayList<>();

    public static void loadServerGroups() {
        for(String serverGroupName : GlobalX.getMainConfig().getSection("ServerGroups").getKeys()) {
            List<String> serverNames = GlobalX.getMainConfig().getStringList("ServerGroups." + serverGroupName + ".servers");
            String permissionRequired = GlobalX.getMainConfig().getString("ServerGroups." + serverGroupName + ".permission-required");
            if (serverNames.isEmpty() || permissionRequired == null) {
                GlobalX.getMainLogger().warning("§7[Globalx] [ServerGroupManager] &cThe group number " + serverGroups.size() + 1 + " has some missing information (Required Information: ServerNames, PermissionRequired)");
                break;
            }

            serverGroups.add(new ServerGroup(serverGroupName, serverNames, permissionRequired));
        }

        GlobalX.getMainLogger().info("§8[GlobalX] [ServerGroupManager] §fLoaded §b" + serverGroups.size() + "§f server groups");
    }

    public ServerGroup getServerGroupByName(String serverGroupName) {
        for (ServerGroup serverGroup : serverGroups) {
            if (Objects.equals(serverGroup.getGroupName(), serverGroupName)) return serverGroup;
        }

        return null;
    }
}
