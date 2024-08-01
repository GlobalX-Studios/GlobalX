package it.omnisys.plugin.managers;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.scheduledmessages.ScheduledMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ScheduledMessageManager {
    private static final Configuration mainConfig = GlobalX.getMainConfig();
    private static final List<ScheduledMessage> scheduledMessages = new ArrayList<>();

    public static void loadScheduledMessages() {
        for (String scheduledMessageName : mainConfig.getSection("ScheduledMessages").getKeys()) {
            String message = mainConfig.getString("ScheduledMessages." + scheduledMessageName + ".Message");
            long timeout = mainConfig.getLong("ScheduledMessages." + scheduledMessageName + ".Timeout");
            boolean repeat = mainConfig.getBoolean("ScheduledMessages." + scheduledMessageName + ".Repeat");
            List<String> servers = mainConfig.getStringList("ScheduledMessages." + scheduledMessageName + ".Servers");
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, mainConfig.getString("ScheduledMessages." + scheduledMessageName + ".Action"));
            String value = mainConfig.getString("ScheduledMessages." + scheduledMessageName + ".Value");
            String hover = mainConfig.getString("ScheduledMessages." + scheduledMessageName + ".Hover");

            ScheduledMessage scheduledMessage = new ScheduledMessage(scheduledMessageName, message, timeout, repeat, servers, clickEvent, value, hover);
            scheduledMessages.add(scheduledMessage);
        }

        GlobalX.getMainLogger().info("§bLoaded §8" + scheduledMessages.size() + " §bScheduled Messages");
    }
}
