package it.omnisys.plugin.managers.chat;

import it.omnisys.plugin.chat.Chat;
import it.omnisys.plugin.chat.PrivateChat;
import lombok.Getter;

public class PrivateChatManager {
    @Getter static Chat privateChat = new PrivateChat();
}
