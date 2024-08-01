package it.omnisys.plugin.group;

import lombok.Getter;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.List;

public class Group {
    @Getter
    private String prefix;
    @Getter
    private Double cooldown;
    @Getter
    private String chatFormat;
    @Getter
    private String permission;
    @Getter
    private List<String> prefixes;
    @Getter
    private String groupName;

    public Group(String prefix, Double cooldown, String chatFormat, String permission, List<String> prefixes, @OptionalBottom String groupName) {
        this.prefix = prefix;
        this.cooldown = cooldown;
        this.chatFormat = chatFormat;
        this.permission = permission;
        this.prefixes = prefixes;
        this.groupName = groupName;
    }
}
