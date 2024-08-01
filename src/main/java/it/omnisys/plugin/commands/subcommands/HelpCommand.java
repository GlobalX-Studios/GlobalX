package it.omnisys.plugin.commands.subcommands;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.commands.AddedCommand;
import it.omnisys.plugin.managers.CommandManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class HelpCommand extends Command implements AddedCommand {
    

    public HelpCommand() {
        super("ghelp", "", "globalhelp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ChatUtils.sendMessage(sender, "\n§7§m-------&r &b&lGLOBALX COMMANDS &r&7(v" + GlobalX.getVersion() + ") &r&7&m------\n");
        for (AddedCommand cmd : CommandManager.getCommandList()) {
            if (!sender.hasPermission(cmd.getPermission()) && !cmd.getPermission().isEmpty()) break;
            ChatUtils.sendMessage(sender, "§7- §b" + cmd.getUsage() + " §8- §7" + cmd.getDescription());
        }
        ChatUtils.sendMessage(sender, "\n§7§m-------------------------------------------------\n");
    }

    @Override
    public String getUsage() {
        return "/globalx help";
    }

    @Override
    public String getDescription() {
        return "Shows this list";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
