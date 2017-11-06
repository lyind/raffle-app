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

import net.talpidae.raffle.app.api.RaffleResult;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


public interface RaffleDao
{
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO quiz (result)\n"
            + "  VALUES (:raffleResult.result)")
    Integer insertRaffleResult(@BindBean("raffleResult") RaffleResult raffleResult);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO quiz_baseline (result)\n"
            + "  VALUES (:raffleBaseline.result)")
    Integer insertRaffleBaseline(@BindBean("raffleBaseline") RaffleResult raffleBaseline);

    @RegisterConstructorMapper(RaffleResult.class)
    @SqlQuery("SELECT * FROM quiz")
    List<RaffleResult> findAll();

    @RegisterConstructorMapper(RaffleResult.class)
    @SqlQuery("SELECT * FROM quiz WHERE id = :id")
    RaffleResult findById(@Bind("id") Integer id);

    @RegisterConstructorMapper(RaffleResult.class)
    @SqlQuery("SELECT *\n"
            + "FROM quiz_baseline qb1\n"
            + "WHERE qb1.id = (\n"
            + "  SELECT id\n"
            + "  FROM (SELECT id, MAX(qb2.ts) FROM quiz_baseline qb2)\n"
            + ")")
    RaffleResult findCurrentRaffleBaseline();
}