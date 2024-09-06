package it.globalx.velocity.module.implementations.playerformat.format;

import com.velocitypowered.api.proxy.Player;
import it.globalx.velocity.module.implementations.playerformat.format.causes.FormatCause;

import java.util.Set;

public record PlayerFormat(String chatFormat, String tabFormat, Set<FormatCause> formatCauses) {

    public boolean isValid(Player player) {
        for (FormatCause formatCause : formatCauses) {
            if (!formatCause.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

}
