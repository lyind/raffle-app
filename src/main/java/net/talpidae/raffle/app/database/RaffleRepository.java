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

import org.jdbi.v3.sqlobject.CreateSqlObject;

import java.util.List;


public interface RaffleRepository
{
    @CreateSqlObject
    RaffleDao raffleDao();


    default Integer insertRaffleResult(RaffleResult raffleResult)
    {
        return raffleDao().insertRaffleResult(raffleResult);
    }


    default Integer insertRaffleBaseline(RaffleResult raffleResult)
    {
        return raffleDao().insertRaffleBaseline(raffleResult);
    }


    default List<RaffleResult> findAll()
    {
        return raffleDao().findAll();
    }


    default RaffleResult findById(Integer id)
    {
        return raffleDao().findById(id);
    }


    default RaffleResult findCurrentRaffleBaseline()
    {
        return raffleDao().findCurrentRaffleBaseline();
    }
}