package it.omnisys.plugin.group;

import lombok.Getter;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.util.List;
import java.util.Objects;

public class ServerGroup {
    @Getter
    private String groupName;
    @Getter
    private List<String> serverNames;
    private final String permissionRequired;

    public ServerGroup(String groupName, List<String> serverNames, @OptionalBottom String permissionRequired) {
        this.groupName = groupName;
        this.serverNames = serverNames;
        this.permissionRequired = permissionRequired;
    }

    public String getPermissionRequired() {
        return Objects.equals(this.permissionRequired, "none") ? null : this.permissionRequired;
    }
}
