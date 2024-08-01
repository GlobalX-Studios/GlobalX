package it.omnisys.plugin.commands.privatemessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageCommand extends Command implements AddedCommand {

    private static final Configuration messageConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();
    @Getter
    private static final HashMap<CommandSender, CommandSender> conversations = new HashMap<>();
    @Getter @Setter
    public static ScheduledTask conversationsAutoDelete;

    public MessageCommand() {
        super("message", PermissionManager.GLOBALX_MESSAGE_SEND, "msg", "tell");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!mainConfig.getBoolean("UsePrivateMessaging") || BlockMessagesCommand.getBlockingPlayers().contains(sender)) {
            ChatUtils.sendMessage(sender, messageConfig.getString("NoPrivateMessagesAllowed"));
            return;
        }

        if (args.length < 2 || !sender.hasPermission(PermissionManager.GLOBALX_MESSAGE_SEND)) {
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
            ChatUtils.handleConsoleMessage(sender, receiveMSG, sendMSG, message);
        } else {
            ChatUtils.handlePlayerMessage(sender, receiverName, receiveMSG, sendMSG, message);
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