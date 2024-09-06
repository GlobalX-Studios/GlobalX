package it.globalx.velocity.module.implementations.playerformat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.globalx.velocity.module.implementations.playerformat.PlayerFormatModule;
import it.globalx.velocity.module.implementations.playerformat.format.PlayerFormat;
import it.globalx.velocity.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

@RequiredArgsConstructor
public class FormatListener {
    private final PlayerFormatModule playerFormatModule;

    @Subscribe
    public void onJoin(ServerConnectedEvent event) {
        Player player = event.getPlayer();

        //todo: setPlayerListName with Spigot Bridge
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerFormat playerFormat = playerFormatModule.getFormatManager().getFormat(player).orElse(null);

        if (playerFormat == null) {
            return;
        }

        event.setResult(PlayerChatEvent.ChatResult.denied());

        RegisteredServer registeredServer = player.getCurrentServer().orElseThrow().getServer();

        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

        String prefix = "", suffix = "";

        if (user != null) {
            if (user.getCachedData().getMetaData().getPrefix() != null) {
                prefix = user.getCachedData().getMetaData().getPrefix();
            } else if (LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getPrefix() != null) {
                prefix = LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getPrefix();
            }
            if (user.getCachedData().getMetaData().getSuffix() != null) {
                suffix = user.getCachedData().getMetaData().getSuffix();
            } else if (LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getSuffix() != null) {
                suffix = LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getSuffix();
            }
        }

        if (prefix == null) {
            prefix = "";
        }

        if (suffix == null) {
            suffix = "";
        }

        String chatFormat = playerFormat.chatFormat()
                .replace("%player%", player.getUsername())
                .replace("%message%", event.getMessage())
                .replace("%luckperms_prefix%", prefix)
                .replace("%luckperms_suffix%", suffix);

        Component message = ChatUtils.colorAndGetComponent(chatFormat);

        registeredServer.getPlayersConnected().forEach(target -> target.sendMessage(message));
    }

}
