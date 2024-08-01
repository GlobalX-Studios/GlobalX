package it.omnisys.plugin.managers;

import it.omnisys.plugin.commands.AddedCommand;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    @Getter
    static List<AddedCommand> commandList = new ArrayList<>();

    public CommandManager() {

    }
}
