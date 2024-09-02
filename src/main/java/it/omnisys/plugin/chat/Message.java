package it.omnisys.plugin.chat;

import lombok.Getter;
import net.md_5.bungee.api.CommandSender;

import javax.annotation.Nullable;
import java.util.Date;

public class Message {
    @Getter private final String messageContent;
    @Getter private final CommandSender author;
    @Getter private final Date sendingDate;
    @Getter @Nullable private final CommandSender receiver;

    public Message(String messageContent, CommandSender author, Date sendingDate, @Nullable CommandSender receiver) {
        this.messageContent = messageContent;
        this.author = author;
        this.sendingDate = sendingDate;
        this.receiver = receiver;
    }
}
