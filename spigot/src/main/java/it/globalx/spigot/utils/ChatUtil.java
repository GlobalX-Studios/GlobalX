package it.globalx.spigot.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ChatUtil {

    public String color(String old) {
        return ChatColor.translateAlternateColorCodes('&', old);
    }

    public List<String> color(List<String> olds) {
        List<String> news = new ArrayList<>();
        for (String old : olds) {
            news.add(color(old));
        }
        return news;
    }

}
