package it.globalx.spigot;

import it.globalx.spigot.socket.GlobalXSocket;
import it.globalx.spigot.utils.ChatUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public final class GlobalXSpigot extends JavaPlugin {
    @Getter
    private GlobalXSpigot instance;
    private GlobalXSocket globalXSocket;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        try {
            globalXSocket = new GlobalXSocket(
                    getConfig().getString("socket.host"),
                    getConfig().getInt("socket.port"),
                    (message) -> {

                    }
            );
        } catch (IOException e) {
            getLogger().info(ChatUtil.color("&7[GlobalX] [Socket] &cError while connecting to the socket: " + e.getMessage()));
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
