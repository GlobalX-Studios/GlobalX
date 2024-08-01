package it.omnisys.plugin;

import it.omnisys.plugin.managers.ConfigManager;
import it.omnisys.plugin.managers.GroupManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.managers.ScheduledMessageManager;
import it.omnisys.plugin.utils.ReflectionUtils;
import it.omnisys.plugin.utils.UpdateChecker;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Logger;

public final class GlobalX extends Plugin {
    @Getter
    static GlobalX plugin;
    @Getter
    static Configuration mainConfig;
    @Getter
    static Configuration messagesConfig;
    @Getter
    static Logger mainLogger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        mainLogger = getProxy().getLogger();

        mainConfig = new ConfigManager("config.yml").create();
        messagesConfig = new ConfigManager("messages.yml").create();
        GroupManager.loadGroups();
        ServerGroupManager.loadServerGroups();
        ScheduledMessageManager.loadScheduledMessages();


        new UpdateChecker(this, 102941).checkVersion();
        
        mainLogger.info("   §b________      __          __   _  __");
        mainLogger.info("  §b/ ____/ /___  / /_  ____ _/ /  | |/ /");
        mainLogger.info(" §b/ / __/ / __ \\/ __ \\/ __ `/ /   |   /   §7Running on Version §8v" + getVersion());
        mainLogger.info("§b/ /_/ / / /_/ / /_/ / /_/ / /   /   |        §7Plugin by §8" + getAuthor());
        mainLogger.info("§b\\____/_/\\____/_.___/\\__,_/_/   /_/|_|     \n");

        ReflectionUtils.registerCommands(this);
        ReflectionUtils.registerListeners(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mainLogger.info("   §b________      __          __   _  __");
        mainLogger.info("  §b/ ____/ /___  / /_  ____ _/ /  | |/ /");
        mainLogger.info(" §b/ / __/ / __ \\/ __ \\/ __ `/ /   |   /   §cDisabling §8v" + getVersion());
        mainLogger.info("§b/ /_/ / / /_/ / /_/ / /_/ / /   /   |        §7Plugin by §8" + getAuthor());
        mainLogger.info("§b\\____/_/\\____/_.___/\\__,_/_/   /_/|_|     \n");
    }

    public static void reload() { plugin.onDisable(); plugin.onEnable(); }
    public static String getVersion() { return plugin.getDescription().getVersion(); }
    public static String getAuthor() { return plugin.getDescription().getAuthor(); }
}
