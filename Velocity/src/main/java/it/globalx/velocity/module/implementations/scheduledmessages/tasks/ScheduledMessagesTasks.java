package it.globalx.velocity.module.implementations.scheduledmessages.tasks;

import com.velocitypowered.api.scheduler.ScheduledTask;
import it.globalx.velocity.GlobalXVelocity;
import it.globalx.velocity.module.implementations.scheduledmessages.ScheduledMessagesModule;
import it.globalx.velocity.module.implementations.scheduledmessages.messages.ScheduledMessage;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ScheduledMessagesTasks implements Consumer<ScheduledTask> {
    private final ScheduledMessagesModule instance;
    private final HashMap<ScheduledMessage, Integer> tickMap = new HashMap<>();

    public ScheduledMessagesTasks(ScheduledMessagesModule instance) {
        this.instance = instance;
        GlobalXVelocity.getInstance().getProxyServer().getScheduler().buildTask(instance.plugin(), this).repeat(1, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        Set<ScheduledMessage> scheduledMessages = instance.getScheduledMessageManager().getScheduledMessages();
        if (scheduledMessages.size() == 0) {
            scheduledTask.cancel();
            return;
        }

        scheduledMessages
                .forEach(scheduledMessage -> {
                    int diff = scheduledMessage.timeoutSeconds() - tickMap.getOrDefault(scheduledMessage, 0);

                    if (diff <= 0) {
                        scheduledMessage.execute();
                        if (scheduledMessage.repeat()) {
                            tickMap.put(scheduledMessage, 0);
                            return;
                        }

                        tickMap.remove(scheduledMessage);
                        instance.getScheduledMessageManager().removeScheduledMessage(scheduledMessage);
                        return;
                    }
                    tickMap.put(scheduledMessage, tickMap.getOrDefault(scheduledMessage, 0) + 1);
                });
    }
}
