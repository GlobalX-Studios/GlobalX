package it.omnisys.plugin.commands.globalchat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

import net.md_5.bungee.config.Configuration;
import org.checkerframework.checker.optional.qual.OptionalBottom;
public class GlobalToggleCommand extends Command implements AddedCommand {

    public static HashMap<CommandSender, @OptionalBottom ServerGroup> globalToggledPlayers = new HashMap<>();

    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();

    public GlobalToggleCommand() {
        super("globaltoggle", PermissionManager.GLOBALX_GLOBALCHAT_TOGGLE, "gtoggle");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(PermissionManager.GLOBALX_GLOBALCHAT_TOGGLE)) {
                ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
                return;
            }

            if (globalToggledPlayers.containsKey(sender)) {
                globalToggledPlayers.remove(sender);
                ChatUtils.sendMessage(sender, messagesConfig.getString("GlobalChatToggledOff"));
            } else {
                globalToggledPlayers.putIfAbsent(sender, null);
                try {
                    globalToggledPlayers.put(sender, new ServerGroupManager().getServerGroupByName(args[0]));
                } catch (IndexOutOfBoundsException ex) {
                    globalToggledPlayers.put(sender, null);
                }
                ChatUtils.sendMessage(sender, messagesConfig.getString("GlobalChatToggledOn"));
            }
    }

    @Override
    public String getUsage() {
        return "/globaltoggle [serverGroup]";
    }

    @Override
    public String getDescription() {
        return "Toggle global chat on/off";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
