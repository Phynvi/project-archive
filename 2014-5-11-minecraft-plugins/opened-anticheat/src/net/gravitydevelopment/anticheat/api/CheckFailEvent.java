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

package net.gravitydevelopment.anticheat.api;

import net.gravitydevelopment.anticheat.check.CheckType;
import net.gravitydevelopment.anticheat.util.User;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player fails an AntiCheat check
 */

public class CheckFailEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final User user;
	private final CheckType type;

	public CheckFailEvent(final User user, final CheckType type) {
		this.user = user;
		this.type = type;
	}

	/**
	 * Get the {@link net.gravitydevelopment.anticheat.util.User} who failed the check
	 *
	 * @return a {@link net.gravitydevelopment.anticheat.util.User}
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Get the {@link net.gravitydevelopment.anticheat.check.CheckType} failed
	 *
	 * @return a {@link net.gravitydevelopment.anticheat.check.CheckType}
	 */
	public CheckType getCheck() {
		return type;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
