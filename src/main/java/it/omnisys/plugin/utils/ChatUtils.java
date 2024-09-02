package it.omnisys.plugin.utils;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.privatemessages.BlockMessagesCommand;
import it.omnisys.plugin.commands.privatemessages.SocialSpyCommand;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;
import it.omnisys.plugin.commands.privatemessages.MessageCommand;

public class ChatUtils {

    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();
    @Getter
    private static final String prefix = messagesConfig.getString("prefix");

    public static String chat(String message) {
        if (message.contains("%prefix%")) return ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix));
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(chat(message)));
    }

    public static String colorLogs(String message) {
        return ChatColor.translateAlternateColorCodes('§', message);
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
}
