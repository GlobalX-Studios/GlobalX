/*
    GlobalX Created by Sgattix & GX_Regent2.0
    Public GNU License 2022

    All rights reserved
 */

package it.omnisys.plugin.managers.config;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import it.omnisys.plugin.GlobalX;
import org.yaml.snakeyaml.error.YAMLException;

public class ConfigManager {
    @Getter
    String configName;
    @Getter
    File configFile;
    @Getter
    Configuration config;

    public ConfigManager(String configName) {
        this.configName = configName;
        this.configFile = new File(GlobalX.getPlugin().getDataFolder(), configName);
    }


    public Configuration create() {
        if (!GlobalX.getPlugin().getDataFolder().exists()) {
            GlobalX.getPlugin().getDataFolder().mkdir();
        }

        File file = this.configFile;
        if (!file.exists()) {
            try {
                InputStream in = GlobalX.getPlugin().getResourceAsStream(configName);
                Files.copy(in, file.toPath(), new CopyOption[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return register();
    }

    public Configuration register() {
        try {
            this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
            return this.config;
        } catch (IOException | YAMLException e) {
            throw new RuntimeException(e);
        }
    }

    public void logToFile(String message) {

        try {
            File dataFolder = GlobalX.getPlugin().getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File saveTo = new File(GlobalX.getPlugin().getDataFolder(), this.configName);
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }


            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(message);

            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
