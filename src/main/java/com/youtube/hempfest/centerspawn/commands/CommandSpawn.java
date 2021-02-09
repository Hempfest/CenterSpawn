package com.youtube.hempfest.centerspawn.commands;

import com.youtube.hempfest.centerspawn.CenterSpawn;
import com.youtube.hempfest.centerspawn.util.Config;
import com.youtube.hempfest.centerspawn.util.Spawn;
import com.youtube.hempfest.centerspawn.util.SpawnManager;
import com.youtube.hempfest.centerspawn.util.SpawnUtil;
import com.youtube.hempfest.clans.construct.Clan;
import com.youtube.hempfest.clans.construct.api.ClansAPI;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
                    if (Clan.action.getClan(p.getUniqueId()) != null) {
                        Clan c = ClansAPI.getInstance().getClan(p.getUniqueId());
                        if (Arrays.asList(c.getMembers()).contains(target.getUniqueId().toString())) {
                            break;
                        }
                    }
                    manager.msg("&c&oAnother player is too close! Teleporting in 10 seconds...");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CenterSpawn.getInstance(), () -> {
                        spawn.teleport();
                        manager.msg("&aWelcome to spawn!");
                        SpawnUtil.spawnProtected.put(p.getUniqueId(), true);
                    }, 20 * 10);
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
