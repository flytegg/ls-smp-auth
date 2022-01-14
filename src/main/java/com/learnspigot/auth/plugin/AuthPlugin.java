package com.learnspigot.auth.plugin;

import com.learnspigot.auth.Auth;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthPlugin extends JavaPlugin {
    private Auth auth;

    @Override
    public void onEnable() {
        auth = new Auth(this);
    }

    @Override
    public void onDisable() {
        auth.shutdown();
    }
}
