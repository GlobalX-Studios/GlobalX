package it.globalx.velocity.module.implementations.playerformat.format.causes;

import com.velocitypowered.api.proxy.Player;
import it.globalx.velocity.module.implementations.playerformat.format.causes.type.FormatCausesType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

public record FormatCause(FormatCausesType type, String param) {

    public boolean isSatisfied(Player player) {
        switch (type) {
            case GROUP -> {
                LuckPerms luckPerms = LuckPermsProvider.get();
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());

                if (user == null) {
                    return false;
                }

                return user.getInheritedGroups(QueryOptions.defaultContextualOptions()).stream()
                        .anyMatch(group -> group.getName().equalsIgnoreCase(param));
            }
            case PERMISSION -> {
                return player.hasPermission(param);
            }
        }
        return false;
    }
}
