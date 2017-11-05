/*
 * Copyright (C) 2017  Jonas Zeiger <jonas.zeiger@talpidae.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.talpidae.raffle.app.database;

import com.google.inject.Singleton;

import net.talpidae.base.database.DefaultDataBaseConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;


@Singleton
@Getter
public class RaffleAppDefaultDataBaseConfig extends DefaultDataBaseConfig
{
    private final int maximumPoolSize = 2;   // safe to use more on SQLite3, since we cleanly close() the pool

    private final String jdbcUrl = "jdbc:sqlite:raffle.db";

    private final String userName = "quiz";

    private final String password = "wetten";

    private final String poolName = "RaffleDatabasePool";

    private final String driverClassName = org.sqlite.JDBC.class.getName();

    private final String connectionTestQuery = "SELECT 1";

    private final int maxLifetime = (int) TimeUnit.MINUTES.toMillis(8);

    private final int idleTimeout = (int) TimeUnit.SECONDS.toMillis(45);

    private final Map<String, String> dataSourceProperties = new HashMap<>();


    public RaffleAppDefaultDataBaseConfig()
    {
        dataSourceProperties.put("cachePrepStmts", "true");
        dataSourceProperties.put("prepStmtCacheSize", "250");
        dataSourceProperties.put("prepStmtCacheSqlLimit", "2048");
        dataSourceProperties.put("cache", "shared");
        dataSourceProperties.put("case_sensitive_like", "ON");
    }
}