package it.omnisys.plugin.managers.permissions;

import lombok.Getter;

public enum Permission {
    GLOBALX_ADMIN("globalx.admin"),
    GLOBALX_RELOAD("globalx.reload"),
    GLOBALX_GLOBALCHAT_USE("globalx.globalchat.use"),
    GLOBALX_GLOBALCHAT_TOGGLE("globalx.globalchat.toggle"),
    GLOBALX_STAFFCHAT_USE("globalx.staffchat.use"),
    GLOBALX_STAFFCHAT_TOGGLE("globalx.staffchat.toggle"),
    GLOBALX_GLOBALCHAT_COOLDOWN_BYPASS("globalx.globalchat.cooldown.bypass"),
    GLOBALX_PRIVATEMSG_SEND("globalx.privatemessage.send"),
    GLOBALX_PRIVATEMSG_RECEIVE("globalx.privatemessage.receive"),
    GLOBALX_PRIVATEMSG_REPLY("globalx.privatemessage.reply"),
    GLOBALX_PRIVATEMSG_BLOCK("globalx.privatemessage.block"),
    GLOBALX_PRIVATEMSG_CONSOLE("globalx.privatemessage.console"),
    GLOBALX_PRIVATEMSG_SOCIALSPY("globalx.privatemessage.socialspy");


    @Getter private final String permission;
    Permission(String permission) {
        this.permission = permission;
    }
}
