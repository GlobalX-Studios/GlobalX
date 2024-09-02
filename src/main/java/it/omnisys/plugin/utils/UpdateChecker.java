package it.omnisys.plugin.utils;

import it.omnisys.plugin.GlobalX;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

        private final GlobalX plugin;
        private final int resourceId;
        private String latestVersion;

        public UpdateChecker(GlobalX plugin, int resourceId) {
            this.plugin = plugin;
            this.resourceId = resourceId;
        }

    public void getVersion(final Consumer<String> consumer) {
        GlobalX.getPlugin().getProxy().getScheduler().runAsync(this.plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

        public void checkVersion(Method method) {
            this.getVersion((version) -> {
                try {
                    method.invoke(version);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
}
