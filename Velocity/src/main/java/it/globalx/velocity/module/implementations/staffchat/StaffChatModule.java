package it.globalx.velocity.module.implementations.staffchat;

import co.aikar.commands.VelocityCommandManager;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.globalx.velocity.module.Module;
import it.globalx.velocity.module.implementations.staffchat.commands.StaffChatCMD;
import it.globalx.velocity.module.implementations.staffchat.listener.StaffChatListener;

public class StaffChatModule extends Module {
    public StaffChatModule() {
        super("staffchat", "Allow the staffs to have a private chat", "StaffChat");
    }

    @Override
    protected void onEnable(Section section) {
        VelocityCommandManager velocityCommandManager = new VelocityCommandManager(plugin().getProxyServer(), plugin());

        velocityCommandManager.registerCommand(new StaffChatCMD(
                section.getString("Permission"),
                section.getString("NoPermission"),
                section.getString("StaffChatEnabled"),
                section.getString("StaffChatDisabled"),
                this
        ));

        plugin().getProxyServer().getEventManager().register(plugin(), new StaffChatListener(
                this,
                section.getString("StaffChatFormat"),
                section.getString("Permission")
        ));
    }

    @Override
    public void onDisable() {

    }
}
