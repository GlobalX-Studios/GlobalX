package it.globalx.velocity.module.implementations.playerformat.manager;

import com.velocitypowered.api.proxy.Player;
import it.globalx.velocity.module.implementations.playerformat.format.PlayerFormat;

import java.util.HashMap;
import java.util.Optional;

public class FormatManager {

    private final HashMap<String, PlayerFormat> formats = new HashMap<>();

    public void addFormat(String name, PlayerFormat format) {
        formats.put(name, format);
    }

    public PlayerFormat getFormat(String name) {
        return formats.get(name);
    }

    public Optional<PlayerFormat> getFormat(Player player) {
        for (PlayerFormat format : formats.values()) {
            if (format.isValid(player)) {
                return Optional.of(format);
            }
        }
        return Optional.empty();
    }

    public void removeFormat(String name) {
        formats.remove(name);
    }

    public boolean hasFormat(String name) {
        return formats.containsKey(name);
    }

    public HashMap<String, PlayerFormat> getFormats() {
        return formats;
    }

}
