package it.omnisys.plugin.utils;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.privatemessages.BlockMessagesCommand;
import it.omnisys.plugin.commands.privatemessages.SocialSpyCommand;
import it.omnisys.plugin.group.Group;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.CoolDownManager;
import it.omnisys.plugin.managers.GroupManager;
import it.omnisys.plugin.managers.PermissionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import it.omnisys.plugin.commands.privatemessages.MessageCommand;

public class ChatUtils {

    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();
    private static final String prefix = messagesConfig.getString("prefix");

    public static String chat(String message) {
        if (message.contains("%prefix%")) {
            return ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(chat(message)));
    }

    public static String colorLogs(String message) {
        return ChatColor.translateAlternateColorCodes('§', message);
    }

    public static String formatGlobalPlaceholders(ProxiedPlayer p, String message, Group group) {

        String globalFormat = group.getChatFormat();
        String groupPrefix = group.getPrefix();
        String serverNameFormat = chat(messagesConfig.getString("ServerNameFormat"));
        String serverName = chat(p.getServer().getInfo().getName());

        globalFormat = chat(globalFormat
                .replace("%prefix%", prefix)
                .replace("%message%", message))
                .replace("%player_prefix%", chat(groupPrefix))
                .replace("%serverNameFormat%", serverNameFormat.replace("%serverName%", serverName))
                .replace("%player_name%", chat(p.getDisplayName()));

        return globalFormat;
    }

    public static String formatGlobalPlaceholders(String message) {
        String globalFormat = chat(messagesConfig.getString("GlobalFormat"));
        String serverNameFormat = chat(messagesConfig.getString("ServerNameFormat"));
        String consoleNameFormat = chat(messagesConfig.getString("ConsoleNameFormat"));
        String consoleServerFormat = chat(messagesConfig.getString("ConsoleServer"));

        return chat(globalFormat
                .replace("%prefix%", prefix)
                .replace("%message%", message))
                .replace("%serverNameFormat%", serverNameFormat.replace("%serverName%", consoleServerFormat))
                .replace("%player_name%", consoleNameFormat);
    }

    public static void broadcastMessage(CommandSender s, TextComponent broadcast, @OptionalBottom ServerGroup serverGroup) {
        if (s instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) s;
            if (mainConfig.getBoolean("HoverAndClickText.Enable")) {
                broadcast.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, mainConfig.getString("HoverAndClickText.Command").replace("%player%", p.getDisplayName()).replace("%target%", p.getServer().getInfo().getName())));
                broadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtils.chat(mainConfig.getString("HoverAndClickText.Text")))));
            }
        }

        if (serverGroup != null) {
            List<Boolean> results = new ArrayList<>();
            for (String serverName : serverGroup.getServerNames()) {
                ServerInfo serverInfo = GlobalX.getPlugin().getProxy().getServerInfo(serverName);

                // Check if the server is online
                if (serverInfo == null) {
                    results.add(false);
                    break;
                }
                results.add(true);


                CoolDownManager.putPlayerInCoolDown(s);
                for (ProxiedPlayer p : serverInfo.getPlayers()) {
                    p.sendMessage(broadcast);
                }
            }
            if (!results.contains(true)) {
                ChatUtils.sendMessage(s, messagesConfig.getString("NoActiveServers"));
                return;
            }
            GlobalX.getPlugin().getProxy().getLogger().info(ChatUtils.colorLogs(broadcast.getText().replaceAll("&", "§")));
            return;
        }

        System.out.println(ProxyServer.getInstance().getPlayers());

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(broadcast);
        }

        GlobalX.getPlugin().getProxy().getLogger().info(ChatUtils.colorLogs(broadcast.getText().replaceAll("&", "§")));

        CoolDownManager.putPlayerInCoolDown(s);
    }

    public static void notifySocialSpies(CommandSender sender, CommandSender receiver, String message) {
        for (CommandSender socialSpy : SocialSpyCommand.getSocialSpyPlayers()) {
            if (socialSpy == sender || socialSpy == receiver) return;

            String notification = messagesConfig.getString("PrivateMessageFormat.SocialSpy")
                    .replace("%player%", sender.getName())
                    .replace("%target%", receiver.getName())
                    .replace("%message%", message);
            if (sender instanceof ProxiedPlayer) {
                notification = notification.replace("%playerServer%", ((ProxiedPlayer) sender).getServer().getInfo().getName());
            } else {
                notification = notification.replace("%playerServer%", messagesConfig.getString("ConsoleServer"));
            }

            if (receiver instanceof ProxiedPlayer) {
                notification = notification.replace("%targetServer%", ((ProxiedPlayer) receiver).getServer().getInfo().getName());
            } else {
                notification = notification.replace("%targetServer%", messagesConfig.getString("ConsoleServer"));
            }
            ChatUtils.sendMessage(socialSpy, notification);
        }
    }

    public static void handleServerGroupMessage(CommandSender sender, String[] args, ServerGroup serverGroup, GroupManager groupManager) {
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        for (String serverName : serverGroup.getServerNames()) {
            ServerInfo server = GlobalX.getPlugin().getProxy().getServerInfo(serverName);
            if (server == null) break;

            TextComponent broadcast = createBroadcast(sender, message, groupManager);
            ChatUtils.broadcastMessage(sender, broadcast, serverGroup);
            return;
        }
        ChatUtils.sendMessage(sender, messagesConfig.getString("NoActiveServers"));
    }

    public static void handleGlobalMessage(CommandSender sender, String message, GroupManager groupManager) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (CoolDownManager.isPlayerInCoolDown(p)) {
                ChatUtils.sendMessage(p, messagesConfig.getString("CantUseInCoolDown"));
                return;
            }

            Group group = groupManager.getPlayerGroup(p);
            if (group == null) {
                ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
                return;
            }
        }

        TextComponent broadcast = createBroadcast(sender, message, groupManager);
        ChatUtils.broadcastMessage(sender, broadcast, null);
    }

    public static TextComponent createBroadcast(CommandSender sender, String message, GroupManager groupManager) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            Group group = groupManager.getPlayerGroup(p);
            return new TextComponent(ChatUtils.formatGlobalPlaceholders(p, message, group));
        } else {
            return new TextComponent(ChatUtils.formatGlobalPlaceholders(message));
        }
    }

    public static void handleConsoleMessage(CommandSender sender, String receiveMSG, String sendMSG, String message) {
        if (!mainConfig.getBoolean("AllowConsolePrivateMessaging.Receiving")) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("UnknownPlayer"));
            return;
        }

        if (!sender.hasPermission(PermissionManager.GLOBALX_MESSAGE_CONSOLE)) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
            return;
        }

        GlobalX.getMainLogger().info(ChatUtils.colorLogs(receiveMSG.replace("%player%", sender.getName()).replace("%server%", messagesConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName()))).replaceAll("&", "§"));
        ChatUtils.sendMessage(sender, sendMSG.replaceAll("%player%", messagesConfig.getString("ConsoleNameFormat")).replaceAll("%server%", messagesConfig.getString("ConsoleServer")));
        MessageCommand.getConversations().put(sender, GlobalX.getPlugin().getProxy().getConsole());
        ChatUtils.notifySocialSpies(sender, GlobalX.getPlugin().getProxy().getConsole(), message);
    }

    public static void handlePlayerMessage(CommandSender sender, String receiverName, String receiveMSG, String sendMSG, String message) {
        ProxiedPlayer receiver = GlobalX.getPlugin().getProxy().getPlayer(receiverName);
        if (receiver == null || !receiver.hasPermission(PermissionManager.GLOBALX_MESSAGE_RECEIVE) || BlockMessagesCommand.getBlockingPlayers().contains(receiver) || receiver == sender) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("UnknownPlayer"));
            return;
        }

        ChatUtils.sendMessage(receiver, receiveMSG.replaceAll("%player%", sender.getName()).replaceAll("%server%", messagesConfig.getString("ServerNameFormat").replaceAll("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName())));
        ChatUtils.sendMessage(sender, sendMSG.replaceAll("%player%", receiverName).replaceAll("%server%", messagesConfig.getString("ServerNameFormat").replaceAll("%serverName%", receiver.getServer().getInfo().getName())));
        MessageCommand.getConversations().put(sender, receiver);
        ChatUtils.notifySocialSpies(sender, receiver, message);

        MessageCommand.setConversationsAutoDelete(GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> MessageCommand.getConversations().remove(sender), mainConfig.getInt("ConversationsAutoDelete"), TimeUnit.SECONDS));
    }
}
