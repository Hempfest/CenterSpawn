package com.youtube.hempfest.centerspawn.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Spawn extends SpawnUtil{

    protected SpawnManager manager;

    protected Config spawn = new Config("Spawn");

    protected String action;

    public Spawn(SpawnManager manager) {
        super(manager);
        this.manager = manager;
    }

    protected FileConfiguration spawn() {
        return spawn.getConfig();
    }

    public void create() {
        Player p = manager.getPlayer();
        spawn().set("Location", p.getLocation());
        action = "Set";
    }

    public void delete() {
        spawn().set("Location", null);
        action = "Removed";
    }

    public void save() {
        Player p = manager.getPlayer();
        spawn.saveConfig();
        manager.msg("&a&oSpawn updated! : " + action + "@ X " + manager.getPlayer().getLocation().getX() + ", Z " + manager.getPlayer().getLocation().getZ());
    }


    public void teleport() {
        Player p = manager.getPlayer();
        p.teleport(spawn().getLocation("Location"));
    }

    public Location getLocation() {
        return spawn().getLocation("Location");
    }




}
