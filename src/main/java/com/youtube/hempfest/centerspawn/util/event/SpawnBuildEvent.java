package com.youtube.hempfest.centerspawn.util.event;

import com.youtube.hempfest.centerspawn.CenterSpawn;
import com.youtube.hempfest.centerspawn.util.SpawnUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnBuildEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private Location loc;
    private boolean cancelled;
    public SpawnBuildEvent(Player p, Location loc) {
        this.p = p;
        this.loc = loc;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public HandlerList getHandlerList() {
        return handlers;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public boolean isCancelled() {return cancelled;}

    public void perform() {
        if (CenterSpawn.isInSpawn(loc)) {
            if (!p.hasPermission("hessentials.spawn.build") && p.getWorld().getName().equals("world")) {
                setCancelled(true);
            }
        }
    }


}
