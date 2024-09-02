package it.omnisys.plugin.chat;

import it.omnisys.plugin.GlobalX;
import it.omnisys.plugin.group.ServerGroup;
import it.omnisys.plugin.managers.CoolDownManager;
import it.omnisys.plugin.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GlobalChat extends Chat {
    public GlobalChat() {
        super("");
    }

    private static final Configuration messagesConfig = GlobalX.getMessagesConfig();
    private static final Configuration mainConfig = GlobalX.getMainConfig();

    @Override
    public void broadcastMessage(CommandSender s, TextComponent broadcast, @OptionalBottom ServerGroup serverGroup) {
        if (s instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) s;
            p.setDisplayName(messagesConfig.getString("NicknameFormat").replace("%playerName%", p.getName()).replace("%message%", broadcast.getText()));
            if (mainConfig.getBoolean("HoverAndClickText.Enable")) {
                broadcast.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, mainConfig.getString("HoverAndClickText.Command").replace("%player%", p.getDisplayName()).replace("%target%", p.getServer().getInfo().getName())));
                broadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtils.chat(mainConfig.getString("HoverAndClickText.Text")))));
            }
        }

        if (serverGroup != null) {
            List<Boolean> results = new ArrayList<>();
            for (String serverName : serverGroup.getServerNames()) {
                ServerInfo serverInfo = GlobalX.getPlugin().getProxy().getServerInfo(serverName);

                // Check if the server is online
                if (serverInfo == null) {
                    results.add(false);
                    break;
                }
                results.add(true);


                CoolDownManager.putPlayerInCoolDown(s);
                for (ProxiedPlayer p : serverInfo.getPlayers()) {
                    p.sendMessage(broadcast);
                }
            }
            if (!results.contains(true)) {
                ChatUtils.sendMessage(s, messagesConfig.getString("NoActiveServers"));
                return;
            }
            GlobalX.getPlugin().getProxy().getLogger().info(ChatUtils.colorLogs(broadcast.getText().replaceAll("&", "§")));
            return;
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(broadcast);
        }

        GlobalX.getPlugin().getProxy().getLogger().info(ChatUtils.colorLogs(broadcast.getText().replaceAll("&", "§")));

        CoolDownManager.putPlayerInCoolDown(s);
        this.getMessages().add(new Message(broadcast.getText(), s, new Date(), null));
    }
}
