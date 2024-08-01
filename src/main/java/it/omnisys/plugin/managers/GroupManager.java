package it.omnisys.plugin.managers;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.group.Group;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {
    static List<Group> groupList = new ArrayList<>();
    static Configuration mainConfig = GlobalX.getMainConfig();

    public GroupManager() {

    }

    public static void loadGroups() {
        for(String group : GlobalX.getMainConfig().getSection("Groups").getKeys()) {
            String prefix = GlobalX.getMainConfig().getString("Groups." + group + ".Prefix");
            Double cooldown = GlobalX.getMainConfig().getDouble("Groups." + group + ".CoolDown");
            String chatFormat = GlobalX.getMainConfig().getString("Groups." + group + ".ChatFormat");
            String permission = GlobalX.getMainConfig().getString("Groups." + group + ".Permission");
            List<String> prefixes = formatChatPrefixes(GlobalX.getMainConfig().getStringList("Groups." + group + ".ChatPrefixes"));

            if (prefix.isEmpty() || cooldown.isNaN() || chatFormat == null || permission == null || prefixes.isEmpty()) {
                GlobalX.getMainLogger().warning("§7[Globalx] [GroupManager] &cThe group number " + groupList.size() + 1 + " has some missing information (ServerNames, PermissionRequired)");
                break;
            }

            Group groupInstance = new Group(prefix, cooldown, chatFormat, permission, prefixes, group);
            groupList.add(groupInstance);
        }

        GlobalX.getMainLogger().info("§8[GlobalX] [GroupManager] §fLoaded §b" + groupList.size() + "§f groups");
    }

    public Group getPlayerGroup(CommandSender sender) {
        for (Group group : groupList) {
            if (sender.hasPermission(group.getPermission())) return group;
        }
        return null;
    }

    public static List<String> formatChatPrefixes(List<String> chatPrefixes) {
        List<String> formattedChatPrefixes = new ArrayList<>();
        for (String str : chatPrefixes) formattedChatPrefixes.add(str.replace("%defaultPrefix%", mainConfig.getString("GlobalChatPrefix.Prefix")));
        return formattedChatPrefixes;
    }
}
