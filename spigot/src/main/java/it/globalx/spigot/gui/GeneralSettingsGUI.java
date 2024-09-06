package it.globalx.spigot.gui;


import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import it.globalx.spigot.gui.hud.HUDInventory;
import org.bukkit.entity.Player;

public class GeneralSettingsGUI extends ChestGui implements HUDInventory {

    public GeneralSettingsGUI() {
        super(45, "");
    }

    @Override
    public void init(Player player) {

    }

    @Override
    public Gui getGui() {
        return this;
    }
}
