package it.omnisys.plugin.chat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.privatemessages.BlockMessagesCommand;
import it.omnisys.plugin.commands.privatemessages.MessageCommand;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.config.ConfigManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class Chat {
    @Getter private final String requiredPermission;
    @Getter private final List<CommandSender> players;
    @Getter private final List<CommandSender> toggledPlayers;
    @Getter private final HashMap<CommandSender, CommandSender> relations;
    @Getter private final HashMap<CommandSender, ServerGroup> senderServerGroup;
    @Getter private final List<Message> messages;
    @Getter @Setter private ScheduledTask conversationsAutoDelete;
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();

    public Chat(String requiredPermission) {
        this.requiredPermission = requiredPermission;
        this.players = GlobalX.getPlugin().getProxy().getPlayers().stream().filter((p) -> p.hasPermission(this.requiredPermission)).collect(Collectors.toList());
        this.toggledPlayers = new ArrayList<>();
        this.relations = new HashMap<>();
        this.senderServerGroup = new HashMap<>();
        this.messages = new ArrayList<>();
    }

    public List<CommandSender> toggle(CommandSender commandSender, String messagePath) {
        boolean toggledStatus = toggledPlayers.contains(commandSender);
        if(toggledStatus) toggledPlayers.remove(commandSender);
        else toggledPlayers.add(commandSender);

        toggledStatus = !toggledStatus;
        ChatUtils.sendMessage(commandSender, GlobalX.getMessagesConfig().getString(messagePath) + (toggledStatus ? ".On" : ".Off"));

        return toggledPlayers;
    }

    public List<CommandSender> insertAllClear() {
        toggledPlayers.clear();
        for (ProxiedPlayer proxiedPlayer : GlobalX.getPlugin().getProxy().getPlayers()) {
            if(proxiedPlayer.hasPermission(this.getRequiredPermission())) toggledPlayers.add(proxiedPlayer);
        }

        return toggledPlayers;
    }

    public void broadcastMessage(String message) {
        for(CommandSender p : players) ChatUtils.sendMessage(p, message);
    }

    public void save(String fileName) {
        String pattern = "MM-dd-yyyy-HH-mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        File theDir = new File(GlobalX.getPlugin().getDataFolder() + "/chat-logs/");
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        ConfigManager config = new ConfigManager("/chat-logs/" + simpleDateFormat.format(new Date()) + "-" + fileName + ".log");
        if(fileName.equalsIgnoreCase("privatechat")) {
            config.logToFile("# " + simpleDateFormat.format(new Date()) + "\n" +
                             "# Private Chat Log \n" +
                             "# Author -> Receiver : Message");
            for(Message message : getMessages()) {
                config.logToFile("[" + simpleDateFormat.format(message.getSendingDate()) + "] [" + fileName + "] " + message.getAuthor() + " -> " + message.getReceiver() + " : " + message.getMessageContent());
            }
            return;
        }
        else if(this instanceof StaffChat)
            config.logToFile("# " + simpleDateFormat.format(new Date()) + "\n" +
                "Staff Chat Log\n" +
                "Author : Message"
        );
        else if(this instanceof GlobalChat) config.logToFile("# " + simpleDateFormat.format(new Date()) + "\n" +
                "Global Chat Log\n" +
                "Author : Message");

        for(Message message : getMessages()) {
            config.logToFile("[" + simpleDateFormat.format(message.getSendingDate()) + "] [" + fileName + "] " + message.getAuthor() + " : " + message.getMessageContent());
        }
    }

    public void handleConsoleMessage(CommandSender sender, String receiveMSG, String sendMSG, String message) {
        // Check if console private messaging is allowed
        if (!mainConfig.getBoolean("AllowConsolePrivateMessaging.Receiving")) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("UnknownPlayer"));
            return;
        }

        // Check if the sender has the required permission
        if (!PermissionManager.hasPermission(sender, Permission.GLOBALX_PRIVATEMSG_SEND)) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
            return;
        }

        // Send the message
        GlobalX.getMainLogger().info(ChatUtils.colorLogs(receiveMSG.replace("%player%", sender.getName()).replace("%server%", messagesConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName()))).replaceAll("&", "§"));
        ChatUtils.sendMessage(sender, sendMSG.replaceAll("%player%", messagesConfig.getString("ConsoleNameFormat")).replaceAll("%server%", messagesConfig.getString("ConsoleServer")));

        // Update the conversation
        MessageCommand.getConversations().put(sender, GlobalX.getPlugin().getProxy().getConsole());

        this.getMessages().add(new Message(message, sender, new Date(), GlobalX.getPlugin().getProxy().getConsole()));

        // Notify social spies
        ChatUtils.notifySocialSpies(sender, GlobalX.getPlugin().getProxy().getConsole(), message);

        setConversationsAutoDelete(GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> MessageCommand.getConversations().remove(sender), mainConfig.getInt("ConversationsAutoDelete"), TimeUnit.SECONDS));
    }

    public void handlePlayerMessage(CommandSender sender, String receiverName, String receiveMSG, String sendMSG, String message) {
        ProxiedPlayer receiver = GlobalX.getPlugin().getProxy().getPlayer(receiverName);
        if (receiver == null || !PermissionManager.hasPermission(receiver, Permission.GLOBALX_PRIVATEMSG_RECEIVE) || BlockMessagesCommand.getBlockingPlayers().contains(receiver) || receiver == sender) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("UnknownPlayer"));
            return;
        }

        ChatUtils.sendMessage(receiver, receiveMSG.replaceAll("%player%", sender.getName()).replaceAll("%server%", messagesConfig.getString("ServerNameFormat").replaceAll("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName())));
        ChatUtils.sendMessage(sender, sendMSG.replaceAll("%player%", receiverName).replaceAll("%server%", messagesConfig.getString("ServerNameFormat").replaceAll("%serverName%", receiver.getServer().getInfo().getName())));
        MessageCommand.getConversations().put(sender, receiver);
        this.getMessages().add(new Message(message, sender, new Date(), receiver));
        ChatUtils.notifySocialSpies(sender, receiver, message);

        setConversationsAutoDelete(GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> MessageCommand.getConversations().remove(sender), mainConfig.getInt("ConversationsAutoDelete"), TimeUnit.SECONDS));
    }

    public abstract void broadcastMessage(CommandSender s, TextComponent broadcast, @OptionalBottom ServerGroup serverGroup);
}
