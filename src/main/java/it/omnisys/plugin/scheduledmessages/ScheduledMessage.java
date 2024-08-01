package it.omnisys.plugin.scheduledmessages;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.utils.ChatUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScheduledMessage {

    private final Configuration mainConfig = GlobalX.getMainConfig();

    @Getter @Setter
    private String messageId;
    @Getter @Setter
    private String message;
    @Getter @Setter
    private TextComponent component;
    @Getter @Setter
    private long timeout;
    @Getter @Setter
    private boolean repeat;
    @Getter @Setter
    private List<String> servers;
    @Getter
    private ScheduledTask task;

    public ScheduledMessage(String messageId, String message, long timeout, boolean repeat, List<String> servers, ClickEvent action, String value, String hover) {
        this.messageId = messageId;
        this.message = message;
        this.timeout = timeout;
        this.repeat = repeat;
        this.servers = servers;
        this.component = new TextComponent(ChatUtils.chat(message));
        this.task = GlobalX.getPlugin().getProxy().getScheduler().schedule(GlobalX.getPlugin(), () -> {
            this.component.setClickEvent(new ClickEvent(action.getAction(), value));
            this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));

            if (servers.contains("*")) {
                GlobalX.getPlugin().getProxy().getPlayers().forEach(p -> p.sendMessage(this.component));
                return;
            }

            for (String server : servers) {
                if (GlobalX.getPlugin().getProxy().getServers().get(server) == null) {
                    GlobalX.getMainLogger().warning("[GlobalX] [ScheduledMessages] Server " + server + " in scheduled message " + this.messageId + " not found, skipping...");
                    return;
                }


                for (ProxiedPlayer p : GlobalX.getPlugin().getProxy().getServers().get(server).getPlayers()) p.sendMessage(this.component);
            }
        }, timeout, repeat ? timeout : 0, TimeUnit.SECONDS);
    }
}
