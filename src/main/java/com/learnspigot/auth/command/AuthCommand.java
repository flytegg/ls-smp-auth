package com.learnspigot.auth.command;

import com.learnspigot.auth.Auth;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record AuthCommand(@NotNull Auth auth) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("auth.toggle")) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command." +
                    " Please contact the server administrators if you believe that this is in error.");
            return true;
        }
        auth.enabled(!auth.enabled());
        sender.sendMessage(ChatColor.YELLOW + "Auth has been " + (auth.enabled() ? "enabled" : "disabled") + ".");
        return true;
    }
}
