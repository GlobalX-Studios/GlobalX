package it.globalx.velocity.utils;

import it.globalx.velocity.GlobalXVelocity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateChecker {

    private final GlobalXVelocity plugin;
    private final int resourceId;

    public UpdateChecker(GlobalXVelocity plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                GlobalXVelocity.getInstance().getLogger().info("Unable to check for updates: " + e.getMessage());
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