package com.stand.utility;

import com.stand.PlayerModification;
import com.stand.exception.WorldNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Common {

	private static final Set<String> blWorlds = new HashSet<>();

	public static String colorize(final String message) {
		return ChatColor.translateAlternateColorCodes('&' , message);
	}

	public static void sendMessage(final Player player , final String message) {
		player.sendMessage(Common.colorize(message));
	}

	public static void sendConsoleMessage(final String message) {
		Bukkit.getConsoleSender().sendMessage(Common.colorize(message));
	}

	public static List<String> getWorldNames() {
		final List<String> worlds = new ArrayList<>();

		for (final World world : Bukkit.getWorlds())
			worlds.add(world.getName());

		return worlds;
	}

	@SneakyThrows
	public static Set<String> getBlackListWorldNames() {

	 	for (final World world : ListUtil.blackListedWorlds)
			 try {
				 blWorlds.add(world.getName());
			 } catch (final NullPointerException ex) {
				 try {
					 Bukkit.dispatchCommand(Bukkit.getConsoleSender() , "pmc reload");
				 } catch (final NullPointerException exception) {
					 Bukkit.getConsoleSender().sendMessage(Common.colorize("&cWorld's name in black-list-world.yml is invalid. Please specific correctly otherwise this plugin won't work."));
					 throw new WorldNotFoundException("Worlds' name in black-list-world.yml is invalid. Please specific correctly otherwise this plugin won't work.");
				 }
			 }

	 	return blWorlds;
	 }

	public static <T extends Runnable> BukkitTask runLater(final T task) {
		return runLater(1, task);
	}

	public static BukkitTask runLater(final int delayTicks, final Runnable task) {
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		final JavaPlugin instance = PlayerModification.getInstance();

		try {
			return runIfDisabled(task) ? null : delayTicks == 0 ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : scheduler.runTask(instance, task) : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : scheduler.runTaskLater(instance, task, delayTicks);
		} catch (final NoSuchMethodError err) {

			return runIfDisabled(task) ? null
					: delayTicks == 0
					? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task))
					: task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task, delayTicks));
		}
	}

	private static boolean runIfDisabled(final Runnable run) {
		if (!PlayerModification.getInstance().isEnabled()) {
			run.run();

			return true;
		}

		return false;
	}

	private static BukkitTask getTaskFromId(final int taskId) {

		for (final BukkitTask task : Bukkit.getScheduler().getPendingTasks())
			if (task.getTaskId() == taskId)
				return task;

		return null;
	}


	public static void fixBlackListNull() {
		blWorlds.add("NullFixer");
	}

	public static void removeNullFixer() {
		blWorlds.remove("NullFixer");
	}

	public static void clearBlackListWorld() {
		blWorlds.clear();
	}


}