package com.youtube.hempfest.centerspawn.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnManager {

    Player p;

    public SpawnManager(Player p) {
        this.p = p;
    }

    // store variables

    public Player getPlayer() {
        return p;
    }

    public void msg(String text) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3&lCortex&7]:&r " + text));
    }


}
