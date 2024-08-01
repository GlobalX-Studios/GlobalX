package it.omnisys.plugin.utils;

import it.omnisys.plugin.GlobalX;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

        private final GlobalX plugin;
        private final int resourceId;

        public UpdateChecker(GlobalX plugin, int resourceId) {
            this.plugin = plugin;
            this.resourceId = resourceId;
        }

        public void getVersion(final Consumer<String> consumer) {
            ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
                try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                } catch (IOException exception) {
                    plugin.getMainLogger().info("Unable to check for updates: " + exception.getMessage());
                }
            });
        }

        public void checkVersion() {
            new UpdateChecker(plugin, resourceId).getVersion(version -> {
                if (plugin.getDescription().getVersion().equals(version)) {
                    plugin.getMainLogger().info("§aWe're up to date!");
                } else {
                    plugin.getMainLogger().info("§aThere's a new Update available (" + version + ")");
                }
            });
        }
}
