package it.omnisys.plugin.commands.subcommands;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class ReloadCommand extends Command implements AddedCommand {
    Configuration messagesConfig = GlobalX.getMessagesConfig();

    public ReloadCommand() {
        super("greload", Permission.GLOBALX_RELOAD.getPermission(), "globalreload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (PermissionManager.hasPermission(sender, Permission.GLOBALX_RELOAD)) {
            GlobalX.reload();
            ChatUtils.sendMessage(sender, messagesConfig.getString("ConfigReloadedMSG"));
        } else {
            ChatUtils.sendMessage(sender, "&b● &7This server is running GlobalX v" + GlobalX.getVersion() + " by &bSgattix");
        }
    }

    @Override
    public String getUsage() {
        return "/globalx reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin configuration files.";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
