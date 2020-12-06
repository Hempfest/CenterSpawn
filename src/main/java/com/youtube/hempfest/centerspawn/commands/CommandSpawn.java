package com.youtube.hempfest.centerspawn.commands;

import com.youtube.hempfest.centerspawn.CenterSpawn;
import com.youtube.hempfest.centerspawn.util.Config;
import com.youtube.hempfest.centerspawn.util.Spawn;
import com.youtube.hempfest.centerspawn.util.SpawnManager;
import com.youtube.hempfest.centerspawn.util.SpawnUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;

public class CommandSpawn extends BukkitCommand {


    public CommandSpawn() {
        super("spawn");
        setAliases(Arrays.asList("cspawn"));
    }

    private void sendMessage(CommandSender player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private String notPlayer() {
        return String.format("[%s] - You aren't a player..", CenterSpawn.getInstance().getDescription().getName());
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(notPlayer());
            return true;
        }
    
        /*
        // VARIABLE CREATION
        //  \/ \/ \/ \/ \/ \/
         */
        int length = args.length;
        Player p = (Player) commandSender;
        Config test = new Config("Spawn");
        FileConfiguration t = test.getConfig();
        /*
        //  /\ /\ /\ /\ /\ /\
        //
         */
        SpawnManager manager = new SpawnManager(p);
        Spawn spawn = new Spawn(manager);
        if (length == 0) {
            for (Entity e : p.getNearbyEntities(30, 30, 30)) {
                if (e instanceof Player) {
                    Player target = (Player) e;
                    manager.msg("&c&oAnother player is too close! Try again later.");
                    return true;
                }
            }
            spawn.teleport();
            manager.msg("&aWelcome to spawn!");
            SpawnUtil.spawnProtected.put(p.getUniqueId(), true);
            return true;
        }
        return false;
    }
}
