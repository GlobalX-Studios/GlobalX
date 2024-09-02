package it.omnisys.plugin.commands.staffchat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.chat.StaffChatManager;
import it.omnisys.plugin.managers.permissions.Permission;
import it.omnisys.plugin.managers.permissions.PermissionManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StaffChatToggleCommand extends Command implements AddedCommand {

    public StaffChatToggleCommand() {
        super("staffchattoggle", Permission.GLOBALX_STAFFCHAT_TOGGLE.getPermission(), "sctoggle");
    }

    @Override
    public String getUsage() {
        return "/staffchattoggle";
    }

    @Override
    public String getDescription() {
        return "Toggle staffchat on/off";
    }

    @Override
    public Command getCommand() {
        return this;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(this.getPermission())) {
            ChatUtils.sendMessage(sender, GlobalX.getMessagesConfig().getString("InsuffArgsMSG"));
            return;
        }

        StaffChatManager.getStaffChat().toggle(sender, "StaffChatToggle");
    }
}
