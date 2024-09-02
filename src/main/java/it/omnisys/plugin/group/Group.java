package it.omnisys.plugin.group;

import lombok.Getter;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.List;

public class Group {
    @Getter private final String prefix;
    @Getter private final Double cooldown;
    @Getter private final String globalChatFormat;
    @Getter private final String permission;
    @Getter private final List<String> prefixes;
    @Getter private final String nickFormat;
    @Getter private final String groupName;
    @Getter private final String staffChatFormat;


    public Group(String prefix, Double cooldown, String globalChatFormat, String staffChatFormat, String permission, List<String> prefixes, String nickFormat, @OptionalBottom String groupName) {
        this.prefix = prefix;
        this.cooldown = cooldown;
        this.globalChatFormat = globalChatFormat;
        this.permission = permission;
        this.prefixes = prefixes;
        this.nickFormat = nickFormat;
        this.groupName = groupName;
        this.staffChatFormat = staffChatFormat;
    }
}
