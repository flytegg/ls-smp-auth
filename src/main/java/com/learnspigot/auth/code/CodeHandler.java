package com.learnspigot.auth.code;

import com.learnspigot.auth.packet.PacketHandler;
import com.mongodb.client.MongoCollection;
import dev.devous.electron.Electron;
import dev.devous.electron.Packet;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class CodeHandler {
    private final @NotNull Map<UUID, Integer> codeMap = new HashMap<>();
    private final @NotNull ScheduledExecutorService scheduledExecutorService;
    private final @NotNull Electron electron;

    public CodeHandler(final @NotNull ScheduledExecutorService scheduledExecutorService,
                       final @NotNull MongoCollection<Document> collection) {
        this.scheduledExecutorService = scheduledExecutorService;
        electron = new Electron(collection, new PacketHandler(this), scheduledExecutorService);
    }

    public void removeIfExists(final @NotNull UUID uid) {
        codeMap.remove(uid);
    }

    public int code(final @NotNull UUID uid) {
        if (codeMap.containsKey(uid)) {
            return codeMap.get(uid);
        }
        return generateCode(uid);
    }

    public @NotNull Optional<UUID> uidFromCode(final int code) {
        for (Map.Entry<UUID, Integer> entry : codeMap.entrySet()) {
            if (entry.getValue().equals(code)) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    private int generateCode(final @NotNull UUID uid) {
        int code = ThreadLocalRandom.current().nextInt(999_999);
        while (codeMap.containsValue(code)) {
            code = ThreadLocalRandom.current().nextInt(999_999);
        }
        codeMap.put(uid, code);
        electron.packetQueue().queue(new Packet(uid, "DISCORD", String.valueOf(code)));
        int finalCode = code;
        scheduledExecutorService.schedule(() -> {
            removeIfExists(uid);
            electron.packetQueue().queue(new Packet(uid, "EXPIRED", String.valueOf(finalCode)));
        }, 30L, TimeUnit.SECONDS);
        return code;
    }
}
