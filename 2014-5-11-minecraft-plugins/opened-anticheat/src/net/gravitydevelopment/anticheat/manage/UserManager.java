/*
 * AntiCheat for Bukkit.
 * Copyright (C) 2012-2014 AntiCheat Team | http://gravitydevelopment.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.gravitydevelopment.anticheat.manage;

import java.util.ArrayList;
import java.util.List;

import net.gravitydevelopment.anticheat.AntiCheat;
import net.gravitydevelopment.anticheat.check.CheckType;
import net.gravitydevelopment.anticheat.config.Configuration;
import net.gravitydevelopment.anticheat.util.Group;
import net.gravitydevelopment.anticheat.util.User;
import net.gravitydevelopment.anticheat.util.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class UserManager {
	private final List<User> users = new ArrayList<User>();
	private final AntiCheatManager manager;
	private final Configuration config;
	private static final ChatColor GRAY = ChatColor.GRAY;
	private static final ChatColor GOLD = ChatColor.GOLD;
	private static final ChatColor RED = ChatColor.RED;

	/**
	 * Initialize the user manager
	 *
	 * @param manager The AntiCheat Manager
	 */
	public UserManager(final AntiCheatManager manager) {
		this.manager = manager;
		this.config = manager.getConfiguration();
	}

	/**
	 * Get a user with the given name
	 *
	 * @param name Name
	 * @return User with name
	 */
	public User getUser(final String name) {
		for (final User user : users) {
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		// Otherwise try to load them
		final User user = new User(name);
		user.setIsWaitingOnLevelSync(true);
		config.getLevels().loadLevelToUser(user);
		return user;
	}

	/**
	 * Get all users
	 *
	 * @return List of users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Add a user to the list
	 *
	 * @param user User to add
	 */
	public void addUser(final User user) {
		users.add(user);
	}

	/**
	 * Remove a user from the list
	 *
	 * @param user User to remove
	 */
	public void remove(final User user) {
		users.remove(user);
	}

	/**
	 * Save a user's level
	 *
	 * @param user User to save
	 */
	public void saveLevel(final User user) {
		config.getLevels().saveLevelFromUser(user);
	}

	/**
	 * Get users in group
	 *
	 * @param group Group to find users of
	 */
	public List<User> getUsersInGroup(final Group group) {
		final List<User> list = new ArrayList<User>();
		for (final User u : users) {
			if (u.getGroup() == group) {
				list.add(u);
			}
		}
		return list;
	}

	/**
	 * Get a user's level, or 0 if the player isn't found
	 *
	 * @param name Name of the player
	 * @return player level
	 */
	public int safeGetLevel(final String name) {
		final User user = getUser(name);
		if (user == null) {
			return 0;
		} else {
			return user.getLevel();
		}
	}

	/**
	 * Set a user's level
	 *
	 * @param name  Name of the player
	 * @param level Group to set
	 */
	public void safeSetLevel(final String name, final int level) {
		final User user = getUser(name);
		if (user != null) {
			user.setLevel(level);
		}
	}

	/**
	 * Reset a user
	 *
	 * @param name Name of the user
	 */
	public void safeReset(final String name) {
		final User user = getUser(name);
		if (user != null) {
			user.resetLevel();
		}
	}

	/**
	 * Get the alert to use for a check being failed
	 *
	 * @return check fail alert message
	 */
	public List<String> getAlert() {
		return config.getLang().alert.getValue();
	}

	/**
	 * Fire an alert
	 *
	 * @param user  The user to alert
	 * @param group The user's group
	 * @param type  The CheckType that triggered the alert
	 */
	public void alert(final User user, final Group group, final CheckType type) {
		final ArrayList<String> messageArray = new ArrayList<String>();
		final List<String> alert = getAlert();
		for (int i = 0; i < alert.size(); i++) {
			String message = alert.get(i);
			if (!message.equals("")) {
				message = message.replaceAll("&player", GOLD + user.getName() + GRAY);
				message = message.replaceAll("&check", GOLD + CheckType.getName(type) + GRAY);
				message = message.replaceAll("&group", group.getColor() + group.getName() + GRAY);
				message = message.replaceAll("&level", "" + user.getLevel() + GRAY);
				messageArray.add(message);
			}
		}
		Utilities.alert(messageArray);
		execute(user, group.getActions(), type);
	}

	/**
	 * Execute configuration actions for an alert
	 *
	 * @param user    The user
	 * @param actions The list of actions to execute
	 * @param type    The CheckType that triggered the alert
	 */
	public void execute(final User user, final List<String> actions, final CheckType type) {
		execute(user, actions, type, config.getLang().kickReason.getValue(), config.getLang().playerWarning.getValue(), config.getLang().banReason.getValue());
	}

	/**
	 * Execute configuration actions for an alert
	 *
	 * @param user       The user
	 * @param actions    The list of actions to execute
	 * @param type       The CheckType that triggered the alert
	 * @param kickReason The config's kick reason
	 * @param warning    The config's warning format
	 * @param banReason  The config's ban reason
	 */
	public void execute(final User user, final List<String> actions, final CheckType type, final String kickReason, final List<String> warning, final String banReason) {
		// Execute synchronously for thread safety when called from AsyncPlayerChatEvent
		Bukkit.getScheduler().scheduleSyncDelayedTask(AntiCheat.getPlugin(), new Runnable() {
			@Override
			public void run() {
				final String name = user.getName();
				for (String event : actions) {
					event = event.replaceAll("&player", name).replaceAll("&world", user.getPlayer().getWorld().getName()).replaceAll("&check", type.name());

					if (event.startsWith("COMMAND[")) {
						for (final String cmd : Utilities.getCommands(event)) {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
						}
					} else if (event.equalsIgnoreCase("KICK")) {
						user.getPlayer().kickPlayer(RED + kickReason);
						final String msg = RED + config.getLang().kickBroadcast.getValue().replaceAll("&player", name) + " (" + CheckType.getName(type) + ")";
						if (!msg.equals("")) {
							manager.log(msg);
							manager.playerLog(msg);
						}
					} else if (event.equalsIgnoreCase("WARN")) {
						final List<String> message = warning;
						for (final String string : message) {
							if (!string.equals("")) {
								user.getPlayer().sendMessage(RED + string);
							}
						}
					} else if (event.equalsIgnoreCase("BAN")) {
						user.getPlayer().setBanned(true);
						user.getPlayer().kickPlayer(RED + banReason);
						final String msg = RED + config.getLang().banBroadcast.getValue().replaceAll("&player", name) + " (" + CheckType.getName(type) + ")";
						if (!msg.equals("")) {
							manager.log(msg);
							manager.playerLog(msg);
						}
					} else if (event.equalsIgnoreCase("RESET")) {
						user.resetLevel();
					}
				}
			}
		});
	}

}
