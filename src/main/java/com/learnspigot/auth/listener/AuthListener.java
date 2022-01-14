package com.learnspigot.auth.listener;

import com.learnspigot.auth.AuthConstant;
import com.learnspigot.auth.code.CodeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public record AuthListener(@NotNull CodeHandler codeHandler) implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final @NotNull PlayerLoginEvent event) {
        if (event.getPlayer().isWhitelisted()) {
            codeHandler.removeIfExists(event.getPlayer().getUniqueId());
            return;
        }
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ((String) AuthConstant.NOT_AUTHENTICATED_MESSAGE.get()).replace("%code%",
                String.valueOf(codeHandler.code(event.getPlayer().getUniqueId()))));
    }
}
