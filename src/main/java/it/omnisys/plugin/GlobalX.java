package it.omnisys.plugin;

import it.omnisys.plugin.managers.chat.GlobalChatManager;
import it.omnisys.plugin.managers.chat.PrivateChatManager;
import it.omnisys.plugin.managers.chat.StaffChatManager;
import it.omnisys.plugin.managers.config.ConfigManager;
import it.omnisys.plugin.managers.permissions.GroupManager;
import it.omnisys.plugin.managers.ServerGroupManager;
import it.omnisys.plugin.managers.chat.ScheduledMessageManager;
import it.omnisys.plugin.utils.ReflectionUtils;
import it.omnisys.plugin.utils.UpdateChecker;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Logger;

public final class GlobalX extends Plugin {
    @Getter static GlobalX plugin;
    @Getter static Configuration mainConfig;
    @Getter static Configuration messagesConfig;
    @Getter static Logger mainLogger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        mainLogger = getProxy().getLogger();

        mainConfig = new ConfigManager("config.yml").create();
        messagesConfig = new ConfigManager("messages.yml").create();
        this.getProxy().getScheduler().runAsync(this, () -> {
            GroupManager.loadGroups();
            ServerGroupManager.loadServerGroups();
            ScheduledMessageManager.loadScheduledMessages();
        });

        new UpdateChecker(this, getResourceId()).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().info("§7[GlobalX] [UpdateChecker] There's a new update available: §b" + version + "§7, you're currently on §b" + getVersion());
            }
        });

        mainLogger.info("   §b________      __          __   _  __");
        mainLogger.info("  §b/ ____/ /___  / /_  ____ _/ /  | |/ /");
        mainLogger.info(" §b/ / __/ / __ \\/ __ \\/ __ `/ /   |   /   §7Running on Version §8v" + getVersion());
        mainLogger.info("§b/ /_/ / / /_/ / /_/ / /_/ / /   /   |        §7Plugin by §8" + getAuthor());
        mainLogger.info("§b\\____/_/\\____/_.___/\\__,_/_/   /_/|_|     \n");

        this.getProxy().getScheduler().runAsync(this, () -> {
            ReflectionUtils.registerCommands(this);
            ReflectionUtils.registerListeners(this);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mainLogger.info("   §b________      __          __   _  __");
        mainLogger.info("  §b/ ____/ /___  / /_  ____ _/ /  | |/ /");
        mainLogger.info(" §b/ / __/ / __ \\/ __ \\/ __ `/ /   |   /   §cDisabling §8v" + getVersion());
        mainLogger.info("§b/ /_/ / / /_/ / /_/ / /_/ / /   /   |        §7Plugin by §8" + getAuthor());
        mainLogger.info("§b\\____/_/\\____/_.___/\\__,_/_/   /_/|_|     \n");

        getProxy().getScheduler().runAsync(this, () -> {
            StaffChatManager.getStaffChat().save("staffchat");
            GlobalChatManager.getGlobalChat().save("globalchat");
            PrivateChatManager.getPrivateChat().save("privatechat");
        });
    }

    public static void reload() { plugin.onDisable(); plugin.onEnable(); }
    public static String getVersion() { return plugin.getDescription().getVersion(); }
    public static String getAuthor() { return plugin.getDescription().getAuthor(); }
    @Getter private static final int resourceId = 102941;
}
