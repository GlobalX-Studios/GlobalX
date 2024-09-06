package it.globalx.spigot.gui.hud;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.entity.Player;

public interface HUDInventory {

    void init(Player player);

    Gui getGui();

    default boolean isStatic() {
        return false;
    }

    default void open(Player player) {
        if (!isStatic())
            init(player);

        getGui().show(player);
    }

}
