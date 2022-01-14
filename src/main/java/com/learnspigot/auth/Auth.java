package com.learnspigot.auth;

import com.learnspigot.auth.code.CodeHandler;
import com.learnspigot.auth.listener.AuthListener;
import com.learnspigot.auth.mongo.MongoDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public final class Auth {
    private final @NotNull MongoDatabase mongoDatabase;

    public Auth(final @NotNull JavaPlugin plugin) {
        this.mongoDatabase = new MongoDatabase(AuthConstant.MONGO_DATABASE_URI.get());
        CodeHandler codeHandler = new CodeHandler(Executors.newSingleThreadScheduledExecutor(), mongoDatabase.getCollection("codes"));
        plugin.getServer().getPluginManager().registerEvents(new AuthListener(codeHandler), plugin);
    }

    public void shutdown() {
        mongoDatabase.close();
    }
}
