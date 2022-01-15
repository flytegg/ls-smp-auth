package com.learnspigot.auth.packet;

import com.learnspigot.auth.code.CodeHandler;
import dev.devous.electron.Packet;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record PacketHandler(@NotNull CodeHandler codeHandler) implements dev.devous.electron.handler.PacketHandler {
    @Override
    public void handle(@NotNull Packet packet) {
        if (!packet.header().equals("WHITELIST")) {
            return;
        }

        Optional<UUID> uid = codeHandler.uidFromCode(NumberUtils.toInt(packet.content()));
        uid.ifPresent(uuid -> {
            Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);
        });
    }
}
