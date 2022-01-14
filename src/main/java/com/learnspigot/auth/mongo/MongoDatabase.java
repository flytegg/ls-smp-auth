package com.learnspigot.auth.mongo;

import com.learnspigot.auth.AuthConstant;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public final class MongoDatabase {
    private final @NotNull MongoClient client;

    public MongoDatabase(final @NotNull String uri) {
        client = new MongoClient(new MongoClientURI(uri));
    }

    public @NotNull MongoCollection<Document> getCollection(final @NotNull String name) {
        return client.getDatabase(AuthConstant.DATABASE_NAME.get()).getCollection(name);
    }

    public void close() {
        client.close();
    }
}
