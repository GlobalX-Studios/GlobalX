package it.omnisys.plugin.commands.privatemessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.chat.PrivateChat;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.chat.PrivateChatManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;
import java.util.HashMap;

public class MessageCommand extends Command implements AddedCommand {

    private static final Configuration messageConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();
    static Chat privateChat = PrivateChatManager.getPrivateChat();
    @Getter
    private static final HashMap<CommandSender, CommandSender> conversations = privateChat.getRelations();

    public MessageCommand() {
        super("message", Permission.GLOBALX_PRIVATEMSG_SEND.getPermission(), "msg", "tell");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!mainConfig.getBoolean("UsePrivateMessaging") || BlockMessagesCommand.getBlockingPlayers().contains(sender)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("NoPrivateMessagesAllowed"));
            return;
        }

        if (args.length < 2 || !PermissionManager.hasPermission(sender, Permission.GLOBALX_PRIVATEMSG_SEND)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("InsuffArgsMessage"));
            return;
        }

        String receiverName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equalsIgnoreCase("console") && BlockMessagesCommand.getBlockingPlayers().contains(GlobalX.getPlugin().getProxy().getConsole())) {
            ChatUtils.sendMessage(sender, messageConfig.getString("PrivateMessagesNotAllowed"));
            return;
        }

        String receiveMSG = messageConfig.getString("PrivateMessageFormat.Receive").replaceAll("%message%", message);
        String sendMSG = messageConfig.getString("PrivateMessageFormat.Send").replaceAll("%message%", message);

        if (args[0].equalsIgnoreCase("console")) {
            privateChat.handleConsoleMessage(sender, receiveMSG, sendMSG, message);
        } else {
            privateChat.handlePlayerMessage(sender, receiverName, receiveMSG, sendMSG, message);
        }
    }

    @Override
    public String getUsage() {
        return "/message <player> <message>";
    }

    @Override
    public String getDescription() {
        return "Send a private message";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}