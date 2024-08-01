package it.omnisys.plugin.commands.subcommands;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class ReloadCommand extends Command implements AddedCommand {
    Configuration messagesConfig = GlobalX.getMessagesConfig();

    public ReloadCommand() {
        super("greload", PermissionManager.GLOBALX_RELOAD, "globalreload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(PermissionManager.GLOBALX_RELOAD)) {
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
