package com.youtube.hempfest.centerspawn;

import com.youtube.hempfest.centerspawn.commands.CommandDelspawn;
import com.youtube.hempfest.centerspawn.commands.CommandSetspawn;
import com.youtube.hempfest.centerspawn.commands.CommandSpawn;
import com.youtube.hempfest.centerspawn.util.Spawn;
import com.youtube.hempfest.centerspawn.util.SpawnManager;
import com.youtube.hempfest.centerspawn.util.SpawnUtil;
import com.youtube.hempfest.centerspawn.util.event.SpawnBuildEvent;
import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Claim;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.events.PlayerPunchPlayerEvent;
import com.youtube.hempfest.clans.util.events.PlayerShootPlayerEvent;
import java.lang.reflect.Field;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class CenterSpawn extends JavaPlugin implements Listener {

	//Instance
	private static CenterSpawn instance;
	private final Logger log = Logger.getLogger("Minecraft");

	//Start server
	public void onEnable() {
		log.info(String.format("[%s] - Spawn plugin by Hempfest!", getDescription().getName()));
		registerCommand(new CommandSpawn());
		registerCommand(new CommandSetspawn());
		registerCommand(new CommandDelspawn());
		instance = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			getServer().getPluginManager().registerEvents(this, this);
		},10L);

		onMoveEvent event = new onMoveEvent();
		event.runTaskTimerAsynchronously(this, 20, 20);
	}

	public void onDisable() {
		log.info(String.format("[%s] - Goodbye friends...", getDescription().getName()));
		SpawnUtil.pastProtect.clear();
		SpawnUtil.spawnProtected.clear();
	}


	public static CenterSpawn getInstance() {
		return instance;
	}

	public void registerCommand(BukkitCommand command) {
		try {

			final Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);

			final CommandMap commandMap = (CommandMap) commandMapField.get(getServer());
			commandMap.register(command.getLabel(), command);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isProtectionZone(Location location) {
		double x = location.getX();
		double z = location.getZ();
		int num = 37;
		int x2 = -num;
		int z2 = -num;
		return x <= num && x >= x2 && z <= num && z >= z2;
	}

	public static boolean isInSpawn(Location location) {
		double x = location.getX();
		double z = location.getZ();
		int num = 260;
		int x2 = -num;
		int z2 = -num;
		return x <= num && x >= x2 && z <= num && z >= z2;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity ent = event.getEntity();
		if (ent instanceof Creeper || ent instanceof Fireball || ent instanceof TNTPrimed) {
			if (isInSpawn(ent.getLocation())) {
				event.setCancelled(true);
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommandUse(PlayerCommandPreprocessEvent event) {
		if (isInSpawn(event.getPlayer().getLocation())) {
			if (event.getMessage().contains("clan claim")) {
				event.setCancelled(true);
			}
			if (event.getMessage().contains("c claim")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (event.getPlayer().getBedSpawnLocation() == null) {
			ClanUtil clanUtil = Clan.clanUtil;
			if (clanUtil.getClan(event.getPlayer()) != null) {
				Clan clan = HempfestClans.clanManager(event.getPlayer());
				if (clan.getBase() != null) {
					event.setRespawnLocation(clan.getBase());
					if (isProtectionZone(event.getPlayer().getLocation())) {
						SpawnUtil.pastProtect.put(event.getPlayer().getUniqueId(), false);
					} else
						SpawnUtil.pastProtect.put(event.getPlayer().getUniqueId(), true);
					return;
				}
			}
			SpawnManager manager = new SpawnManager(event.getPlayer());
			Spawn spawn = new Spawn(manager);
			event.setRespawnLocation(spawn.getLocation());
			SpawnUtil.spawnProtected.put(event.getPlayer().getUniqueId(), true);
			SpawnUtil.pastProtect.put(event.getPlayer().getUniqueId(), false);
			manager.msg("&aWelcome to spawn! Try not to die again..");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!Claim.claimUtil.isInClaim(event.getEntity().getLocation())) {
			if (isInSpawn(event.getEntity().getLocation())) {
				event.getEntity().remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBucketRelease(PlayerBucketEmptyEvent event) {
		SpawnBuildEvent e = new SpawnBuildEvent(event.getPlayer(), event.getBlock().getLocation());
		Bukkit.getPluginManager().callEvent(e);
		e.perform();
		event.setCancelled(e.isCancelled());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBucketFill(PlayerBucketFillEvent event) {
		SpawnBuildEvent e = new SpawnBuildEvent(event.getPlayer(), event.getBlock().getLocation());
		Bukkit.getPluginManager().callEvent(e);
		e.perform();
		event.setCancelled(e.isCancelled());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		SpawnBuildEvent e = new SpawnBuildEvent(event.getPlayer(), event.getBlock().getLocation());
		Bukkit.getPluginManager().callEvent(e);
		e.perform();
		if (e.isCancelled()) {
			event.setCancelled(e.isCancelled());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFirstJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CenterSpawn.getInstance(), () -> {
				SpawnManager manager = new SpawnManager(event.getPlayer());
				Spawn spawn = new Spawn(manager);
				spawn.teleport();
				SpawnUtil.spawnProtected.put(event.getPlayer().getUniqueId(), true);
			}, 2L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		SpawnBuildEvent e = new SpawnBuildEvent(event.getPlayer(), event.getBlock().getLocation());
		Bukkit.getPluginManager().callEvent(e);
		e.perform();
		event.setCancelled(e.isCancelled());
	}

	public static class onMoveEvent extends BukkitRunnable {
		@Override
		public void run() {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!isProtectionZone(p.getLocation())) {
					if (!SpawnUtil.spawnProtected.containsKey(p.getUniqueId()) || SpawnUtil.spawnProtected.get(p.getUniqueId())) {
						SpawnUtil.spawnProtected.put(p.getUniqueId(), false);
						SpawnManager manager = new SpawnManager(p);
						manager.msg("&c&oYou are no longer spawn protected.");
					}
					if (!isInSpawn(p.getLocation())) {
						if (!SpawnUtil.pastProtect.containsKey(p.getUniqueId()) || !SpawnUtil.pastProtect.get(p.getUniqueId())) {
							SpawnUtil.pastProtect.put(p.getUniqueId(), true);
							SpawnManager manager = new SpawnManager(p);
							manager.msg("&4&oYou can now build and are free to claim land. Be careful of locals..");
						}
					} else {
						if (SpawnUtil.pastProtect.containsKey(p.getUniqueId()) && SpawnUtil.pastProtect.get(p.getUniqueId())) {
							SpawnUtil.pastProtect.put(p.getUniqueId(), false);
							SpawnManager manager = new SpawnManager(p);
							manager.msg("&c&oYou are now within spawn area.. building and claiming prohibited.");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof  Player) {
			Player target = (Player) e.getTarget();
			if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && SpawnUtil.spawnProtected.get(target.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPunch(PlayerPunchPlayerEvent e) {
		Player target = e.getVictim();
		Player p = e.getAttacker();
		SpawnManager manager = new SpawnManager(p);
		if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && SpawnUtil.spawnProtected.get(target.getUniqueId())) {
			e.setCanHurt(false);
			manager.msg("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
		}
		if (SpawnUtil.spawnProtected.containsKey(p.getUniqueId()) && SpawnUtil.spawnProtected.get(p.getUniqueId())) {
			if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && !SpawnUtil.spawnProtected.get(target.getUniqueId())) {
				SpawnUtil.spawnProtected.put(p.getUniqueId(), false);

				manager.msg("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
			}
			if (!SpawnUtil.spawnProtected.containsKey(target.getUniqueId())) {
				SpawnUtil.spawnProtected.put(p.getUniqueId(), false);
				manager.msg("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onProjectile(PlayerShootPlayerEvent e) {
		Player p = e.getShooter();
		Player target = e.getShot();
		SpawnManager manager = new SpawnManager(p);
		if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && SpawnUtil.spawnProtected.get(target.getUniqueId())) {
			e.setCanHurt(false);
			manager.msg("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
		}
		if (SpawnUtil.spawnProtected.containsKey(p.getUniqueId()) && SpawnUtil.spawnProtected.get(p.getUniqueId())) {
			if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && !SpawnUtil.spawnProtected.get(target.getUniqueId())) {
				SpawnUtil.spawnProtected.put(p.getUniqueId(), false);
				manager.msg("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
			}
			if (!SpawnUtil.spawnProtected.containsKey(target.getUniqueId())) {
				SpawnUtil.spawnProtected.put(p.getUniqueId(), false);
				manager.msg("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		/*
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player target = (Player) event.getEntity();
			Player p = (Player) event.getDamager();



		}

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile && (
				(Projectile) event.getDamager()).getShooter() instanceof Player) {
			Projectile pr = (Projectile) event.getDamager();
			Player p = (Player) pr.getShooter();
			Player target = (Player) event.getEntity();

		}

		 */


		if (event.getEntity() instanceof Player && event.getDamager() instanceof Monster) {
			Player target = (Player) event.getEntity();
			if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && SpawnUtil.spawnProtected.get(target.getUniqueId())) {
				event.setCancelled(true);
			}
		}

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile && (
				(Projectile) event.getDamager()).getShooter() instanceof Monster) {
			Player target = (Player) event.getEntity();
			if (SpawnUtil.spawnProtected.containsKey(target.getUniqueId()) && SpawnUtil.spawnProtected.get(target.getUniqueId())) {
				event.setCancelled(true);
			}
		}

	}


}
