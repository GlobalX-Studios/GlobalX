package it.omnisys.plugin.commands;

import it.omnisys.plugin.commands.subcommands.HelpCommand;
import it.omnisys.plugin.commands.subcommands.ReloadCommand;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import it.omnisys.plugin.GlobalX;

public class GlobalXCommand extends Command implements AddedCommand {
    public GlobalXCommand() {
        super("globalx", "", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent(ChatUtils.chat("&b● &7This server is running GlobalX v" + GlobalX.getVersion() + " by &bSgattix")));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            new ReloadCommand().execute(sender, args);
        } else if (args[0].equalsIgnoreCase("help")) {
            new HelpCommand().execute(sender, args);
        }
    }

    @Override
    public String getUsage() {
        return "/globalx [reload|help]";
    }

    @Override
    public String getDescription() {
        return "GlobalX main command";
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
