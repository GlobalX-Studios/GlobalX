package it.omnisys.plugin.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public interface AddedCommand {
    void execute(CommandSender sender, String[] args);
    String getPermission();
    String getUsage();
    String getDescription();
    Command getCommand();
}
