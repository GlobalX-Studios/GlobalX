package it.globalx.velocity.module.implementations.scheduledmessages.messages;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.globalx.velocity.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.List;
import java.util.Set;

public record ScheduledMessage(String message, int timeoutSeconds, boolean repeat, Set<RegisteredServer> servers,
                               boolean actionEnabled, ClickEvent.Action action, String actionValue, boolean hoverEnabled,
                               List<String> hoverValue) {
    public void execute() {
        Component cmp = ChatUtils.colorAndGetComponent(message);

        if (actionEnabled) {
            cmp = cmp.clickEvent(ClickEvent.clickEvent(action, actionValue));
        }

        if (hoverEnabled) {
            cmp = cmp.hoverEvent(ChatUtils.colorAndGetComponent(String.join("\n", hoverValue)));
        }

        Component finalCmp = cmp;
        servers.forEach(server -> {
            server.sendMessage(finalCmp);
        });
    }
}
