package it.omnisys.plugin.commands.privatemessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class SocialSpyCommand extends Command implements AddedCommand {
    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    @Getter
    private static final List<CommandSender> socialSpyPlayers = new ArrayList<>();

    public SocialSpyCommand() {
        super("socialspy", Permission.GLOBALX_PRIVATEMSG_SOCIALSPY.getPermission(), "sspy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!PermissionManager.hasPermission(sender, Permission.GLOBALX_PRIVATEMSG_SOCIALSPY)) {
            ChatUtils.sendMessage(sender, messagesConfig.getString("NoPermsMSG"));
            return;
        }

        if (socialSpyPlayers.contains(sender)) {
            socialSpyPlayers.remove(sender);
            ChatUtils.sendMessage(sender, messagesConfig.getString("SocialSpyToggledOff"));
        } else {
            socialSpyPlayers.add(sender);
            ChatUtils.sendMessage(sender, messagesConfig.getString("SocialSpyToggledOn"));
        }
    }

    @Override
    public String getUsage() {
        return "/socialspy";
    }

    @Override
    public String getDescription() {
        return "Toggle SocialSpy";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
