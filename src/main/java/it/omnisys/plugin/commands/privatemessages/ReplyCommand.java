package it.omnisys.plugin.commands.privatemessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReplyCommand extends Command implements AddedCommand {

    Configuration messageConfig = GlobalX.getMessagesConfig();
    Configuration mainConfig = GlobalX.getMainConfig();

    public ReplyCommand() {
        super("reply", PermissionManager.GLOBALX_MESSAGE_REPLY, "r", "rep");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionManager.GLOBALX_MESSAGE_REPLY)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("NoPermsMSG"));
            return;
        }

        if (!MessageCommand.getConversations().containsKey(sender) && !MessageCommand.getConversations().containsValue(sender)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("NoActiveConversations"));
            return;
        }

        List<String> messageList = new ArrayList<>(Arrays.asList(args));
        String message = String.join(" ", messageList);

        String receiveMSG = messageConfig.getString("PrivateMessageFormat.Receive").replaceAll("%message%", message);
        String sendMSG = messageConfig.getString("PrivateMessageFormat.Send").replaceAll("%message%", message);

        CommandSender receiver = MessageCommand.getConversations().get(sender);

        if (receiver == null) {
            receiver = MessageCommand.getConversations().entrySet().stream().filter(entry -> entry.getValue().equals(sender)).findFirst().map(Map.Entry::getKey).orElse(null);
            if (receiver == null) {
                ChatUtils.sendMessage(sender, messageConfig.getString("NoActiveConversations"));
                return;
            }
        }

        if (BlockMessagesCommand.getBlockingPlayers().contains(receiver)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("PrivateMessagesNotAllowed"));
            return;
        }

        if (receiver instanceof ProxiedPlayer && !(sender instanceof ProxiedPlayer)) {
            ChatUtils.sendMessage(receiver, receiveMSG.replace("%player%", messageConfig.getString("ConsoleNameFormat")).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", messageConfig.getString("ConsoleServer"))));
            ChatUtils.sendMessage(sender, sendMSG.replace("%player%", ((ProxiedPlayer) receiver).getDisplayName()).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) receiver).getServer().getInfo().getName())));
        } else if (sender instanceof ProxiedPlayer && !(receiver instanceof ProxiedPlayer)) {
            ChatUtils.sendMessage(receiver, receiveMSG.replace("%player%", ((ProxiedPlayer) sender).getDisplayName()).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName())));
            ChatUtils.sendMessage(sender, sendMSG.replace("%player%", messageConfig.getString("ConsoleNameFormat")).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", messageConfig.getString("ConsoleServer"))));
        } else {
            assert receiver instanceof ProxiedPlayer;
            ChatUtils.sendMessage(sender, sendMSG.replace("%player%", ((ProxiedPlayer) receiver).getDisplayName()).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) receiver).getServer().getInfo().getName())));
            ChatUtils.sendMessage(receiver, receiveMSG.replace("%player%", ((ProxiedPlayer) sender).getDisplayName()).replace("%server%", messageConfig.getString("ServerNameFormat").replace("%serverName%", ((ProxiedPlayer) sender).getServer().getInfo().getName())));
        }

        if (MessageCommand.conversationsAutoDelete != null) {
            MessageCommand.conversationsAutoDelete.cancel();
        }
        MessageCommand.conversationsAutoDelete = GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> {
            MessageCommand.getConversations().remove(sender);
        }, mainConfig.getInt("ConversationsAutoDelete"), TimeUnit.SECONDS);
    }

    @Override
    public String getUsage() {
        return "/reply <message>";
    }

    @Override
    public String getDescription() {
        return "Reply to the last private message";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
