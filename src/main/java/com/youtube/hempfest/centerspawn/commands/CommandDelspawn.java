package com.youtube.hempfest.centerspawn.commands;

import com.youtube.hempfest.centerspawn.CenterSpawn;
import com.youtube.hempfest.centerspawn.util.Config;
import com.youtube.hempfest.centerspawn.util.Spawn;
import com.youtube.hempfest.centerspawn.util.SpawnManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandDelspawn extends BukkitCommand {


    public CommandDelspawn() {
        super("delspawn");
        setAliases(Arrays.asList("dcspawn"));
        setPermission("hessentials.spawn.delete");
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
            if (!p.hasPermission(this.getPermission())) {
                manager.msg("&c&oYou do not have permission to perform this command.");
                return true;
            }
            spawn.delete();
            spawn.save();
            return true;
        }
        return false;
    }
}
