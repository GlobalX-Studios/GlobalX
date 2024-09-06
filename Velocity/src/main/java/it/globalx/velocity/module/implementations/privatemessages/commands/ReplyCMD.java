package it.globalx.velocity.module.implementations.privatemessages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import com.velocitypowered.api.proxy.Player;
import it.globalx.velocity.module.implementations.privatemessages.PrivateMessagesModule;
import it.globalx.velocity.module.implementations.privatemessages.utils.PrivateMessageUtils;
import it.globalx.velocity.utils.ChatUtils;
import lombok.RequiredArgsConstructor;

@CommandAlias("reply|r")
@RequiredArgsConstructor
public class ReplyCMD extends BaseCommand {
    private final PrivateMessagesModule privateMessagesModule;
    private final String permission, noPermissionMessage, mustSpecifyMessage, noOneToReplyTo, senderFormat, receiverFormat;

    @HelpCommand
    @Default
    public void onDefault(Player player, String[] args) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatUtils.colorAndGetComponent(noPermissionMessage));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatUtils.colorAndGetComponent(mustSpecifyMessage));
            return;
        }

        if (!PrivateMessageUtils.hasLastMessage(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorAndGetComponent(noOneToReplyTo));
            return;
        }

        Player target = privateMessagesModule.plugin().getProxyServer().getPlayer(PrivateMessageUtils.getLastMessage(player.getUniqueId())).orElse(null);

        if (target == null) {
            player.sendMessage(ChatUtils.colorAndGetComponent(noOneToReplyTo));
            return;
        }

        StringBuilder message = new StringBuilder();

        for (String arg : args) {
            message.append(arg).append(" ");
        }

        player.sendMessage(
                ChatUtils.colorAndGetComponent(
                        senderFormat
                                .replace("%target%", target.getUsername())
                                .replace("%message%", message.toString())
                )
        );

        target.sendMessage(
                ChatUtils.colorAndGetComponent(
                        receiverFormat
                                .replace("%sender%", player.getUsername())
                                .replace("%message%", message.toString())
                )
        );

        PrivateMessageUtils.setLastMessage(player.getUniqueId(), target.getUniqueId());
        PrivateMessageUtils.setLastMessage(target.getUniqueId(), player.getUniqueId());
    }

}
