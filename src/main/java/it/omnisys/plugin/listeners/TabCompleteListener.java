package it.omnisys.plugin.listeners;

import it.omnisys.plugin.GlobalX;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompleteListener implements Listener {
    @EventHandler
    public void TabCompleteEvent(TabCompleteEvent e) {
        if (e.getCursor().split(" ").length >= 2) return;
        if(e.getCursor().startsWith("/msg") || e.getCursor().startsWith("/tell") || e.getCursor().startsWith("/message")) {
            for(ProxiedPlayer p : GlobalX.getPlugin().getProxy().getPlayers()) {
                e.getSuggestions().add(p.getDisplayName());
            }
        }
    }
}
