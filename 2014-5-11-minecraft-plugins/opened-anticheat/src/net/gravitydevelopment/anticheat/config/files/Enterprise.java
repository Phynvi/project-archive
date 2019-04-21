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

package net.gravitydevelopment.anticheat.config.files;

import net.gravitydevelopment.anticheat.AntiCheat;
import net.gravitydevelopment.anticheat.config.Configuration;
import net.gravitydevelopment.anticheat.config.ConfigurationFile;
import net.gravitydevelopment.anticheat.util.enterprise.Database;

public class Enterprise extends ConfigurationFile {

	public static final String FILENAME = "enterprise.yml";

	public ConfigValue<String> serverName;

	public ConfigValue<Boolean> loggingEnabled;
	public ConfigValue<String> loggingLife;
	public ConfigValue<String> loggingInterval;

	public ConfigValue<Boolean> syncLevels;
	public ConfigValue<String> syncInterval;

	public ConfigValue<Boolean> configGroups;
	public ConfigValue<Boolean> configRules;

	public Database database;

	public Enterprise(final AntiCheat plugin, final Configuration config) {
		super(plugin, config, FILENAME);
	}

	@Override
	public void open() {
		serverName = new ConfigValue<String>("server.name");

		loggingEnabled = new ConfigValue<Boolean>("log.enable");
		loggingLife = new ConfigValue<String>("log.life");
		loggingInterval = new ConfigValue<String>("log.interval");

		syncLevels = new ConfigValue<Boolean>("sync.levels");
		syncInterval = new ConfigValue<String>("sync.interval");

		configGroups = new ConfigValue<Boolean>("config.groups");
		configRules = new ConfigValue<Boolean>("config.rules");

		final ConfigValue<String> databaseType = new ConfigValue<String>("database.type");
		final ConfigValue<String> databaseHostname = new ConfigValue<String>("database.hostname");
		final ConfigValue<String> databaseUsername = new ConfigValue<String>("database.username");
		final ConfigValue<String> databasePassword = new ConfigValue<String>("database.password");
		final ConfigValue<String> databasePrefix = new ConfigValue<String>("database.prefix");
		final ConfigValue<String> databaseSchema = new ConfigValue<String>("database.database");
		final ConfigValue<Integer> databasePort = new ConfigValue<Integer>("database.port");

		if (getConfiguration().getConfig().enterprise.getValue()) {
			// Convert database values to Database
			database = new Database(
					Database.DatabaseType.valueOf(databaseType.getValue()),
					databaseHostname.getValue(),
					databasePort.getValue(),
					databaseUsername.getValue(),
					databasePassword.getValue(),
					databasePrefix.getValue(),
					databaseSchema.getValue(),
					serverName.getValue(),
					loggingInterval.getValue(),
					loggingLife.getValue(),
					syncLevels.getValue(),
					syncInterval.getValue()
					);

			database.connect();
		}
	}
}
