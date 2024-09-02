package it.omnisys.plugin.managers.chat;

import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.chat.StaffChat;
import lombok.Getter;

public class StaffChatManager {
    @Getter
    static Chat staffChat = new StaffChat();
}
