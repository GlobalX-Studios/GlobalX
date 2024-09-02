package it.omnisys.plugin.managers.chat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.chat.GlobalChat;
import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.CoolDownManager;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;

public class GlobalChatManager {
    @Getter
    static Chat globalChat = new GlobalChat();

    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();

    public static void handleServerGroupMessage(CommandSender sender, String[] args, ServerGroup serverGroup, GroupManager groupManager) {
        String message = String.join(" ", args.clone());

        for (String serverName : serverGroup.getServerNames()) {
            ServerInfo server = GlobalX.getPlugin().getProxy().getServerInfo(serverName);
            if (server == null) break;

            TextComponent broadcast = createBroadcast(sender, message, groupManager);
            globalChat.broadcastMessage(sender, broadcast, serverGroup);
            return;
        }
        ChatUtils.sendMessage(sender, messagesConfig.getString("NoActiveServers"));
    }

    public static TextComponent createBroadcast(CommandSender sender, String message, GroupManager groupManager) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            Group group = groupManager.getPlayerGroup(player);
            if(GlobalX.getLuckPerms() != null) {
                User user = GlobalX.getLuckPerms().getUserManager().getUser(player.getUniqueId());
                if(user != null) {
                    String previousDisplayName = player.getDisplayName();
                    StringBuilder finalDisplayName = new StringBuilder();
                    finalDisplayName.append(group.getNickFormat())
                            .append(group.getPrefix())
                            .append(player.getName())
                            .append(message);
                    MetaNode displayName = MetaNode.builder("displayname", finalDisplayName.toString()).build();
                    user.data().add(displayName);
                    GlobalX.getLuckPerms().getUserManager().saveUser(user);
                    player.setDisplayName(finalDisplayName.toString());

                    GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> {
                        MetaNode reset = MetaNode.builder("displayname", previousDisplayName).build();
                        user.data().add(reset);
                        GlobalX.getLuckPerms().getUserManager().saveUser(user);
                        player.setDisplayName(previousDisplayName);
                    }, GlobalX.getMainConfig().getInt("NicknameReset"), TimeUnit.SECONDS);
                }
            }
            return new TextComponent(formatGlobalPlaceholders(player, message, group));
        } else {
            return new TextComponent(formatGlobalPlaceholders(message));
        }
    }

    public static void handleGlobalMessage(CommandSender sender, String message, GroupManager groupManager) {
    // Check if the sender is a ProxiedPlayer
    if (sender instanceof ProxiedPlayer) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        // Check if the player is in cooldown
        if (CoolDownManager.isPlayerInCoolDown(player)) {
            ChatUtils.sendMessage(player, messagesConfig.getString("CantUseInCoolDown"));
            return;
        }

        // Get the player's group
        Group group = groupManager.getPlayerGroup(player);

        // Check if the group is null
        if (group == null) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
            return;
        }
    }

    // Create the broadcast message
    TextComponent broadcast = createBroadcast(sender, message, groupManager);
    if(sender instanceof ProxiedPlayer && GlobalX.getPlaceholderAPI() != null) {
        GlobalX.getPlaceholderAPI().formatPlaceholders(broadcast.getText(), ((ProxiedPlayer) sender).getUniqueId()).thenAccept(broadcast::setText);
    }

    // Broadcast the message
    globalChat.broadcastMessage(sender, broadcast, null);
}

    public static String formatGlobalPlaceholders(ProxiedPlayer p, String message, Group group) {

        String globalFormat = ChatUtils.chat(group.getGlobalChatFormat());
        String groupPrefix = ChatUtils.chat(group.getPrefix());
        String serverNameFormat = ChatUtils.chat(messagesConfig.getString("ServerNameFormat"));
        String serverName = ChatUtils.chat(p.getServer().getInfo().getName());

        globalFormat = ChatUtils.chat(globalFormat
                .replace("%prefix%", ChatUtils.getPrefix())
                .replace("%message%", ChatUtils.chat(message)))
                .replace("%player_prefix%", ChatUtils.chat(groupPrefix))
                .replace("%serverNameFormat%", ChatUtils.chat(serverNameFormat.replace("%serverName%", serverName)))
                .replace("%player_name%", p.getName());

        return globalFormat;
    }

    public static String formatGlobalPlaceholders(String message) {
        String globalFormat = messagesConfig.getString("GlobalFormat");
        String serverNameFormat = messagesConfig.getString("ServerNameFormat");
        String consoleNameFormat = messagesConfig.getString("ConsoleNameFormat");
        String consoleServerFormat = messagesConfig.getString("ConsoleServer");

        return ChatUtils.chat(globalFormat
                .replace("%prefix%", ChatUtils.getPrefix())
                .replace("%message%", message))
                .replace("%serverNameFormat%", serverNameFormat.replace("%serverName%", consoleServerFormat))
                .replace("%player_name%", consoleNameFormat);
    }
}
