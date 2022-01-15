package com.learnspigot.auth;

import com.learnspigot.auth.code.CodeHandler;
import com.learnspigot.auth.command.AuthCommand;
import com.learnspigot.auth.listener.AuthListener;
import com.learnspigot.auth.mongo.MongoDatabase;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Auth {
    private final @NotNull MongoDatabase mongoDatabase;
    private final @NotNull ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private boolean enabled;

    public Auth(final @NotNull JavaPlugin plugin) {
        this.mongoDatabase = new MongoDatabase(AuthConstant.MONGO_DATABASE_URI.get());
        Objects.requireNonNull(plugin.getCommand("toggleauth")).setExecutor(new AuthCommand(this));
        enabled = isEnabled();
        plugin.getServer().setWhitelist(enabled);
        plugin.getLogger().info(enabled ?
                "Auth is enabled, you must have verified to join." :
                "Auth is disabled, anyone can join.");
        CodeHandler codeHandler = new CodeHandler(scheduledExecutorService, mongoDatabase.getCollection("codes"));
        plugin.getServer().getPluginManager().registerEvents(new AuthListener(this, codeHandler), plugin);
    }

    private boolean isEnabled() {
        return Objects.requireNonNull(mongoDatabase.getCollection("config")
                        .find()
                        .first())
                .getBoolean("enabled");
    }

    public void shutdown() {
        mongoDatabase.close();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
        scheduledExecutorService.schedule(() -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection("config");
            Document document = Objects.requireNonNull(collection.find().first());
            document.put("enabled", enabled);
            collection.deleteMany(new BasicDBObject());
            collection.insertOne(document);
        }, 0L, TimeUnit.MILLISECONDS);
    }
}
