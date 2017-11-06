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

package net.talpidae.raffle.app;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import net.talpidae.base.Base;
import net.talpidae.base.database.DataBaseConfig;
import net.talpidae.base.database.DefaultDataBaseConfig;
import net.talpidae.base.util.Application;
import net.talpidae.base.util.auth.Authenticator;
import net.talpidae.base.util.session.SessionService;
import net.talpidae.raffle.app.database.RaffleAppDefaultDataBaseConfig;
import net.talpidae.raffle.app.database.RaffleRepository;
import net.talpidae.raffle.app.util.auth.LocalAuthenticator;
import net.talpidae.raffle.app.util.session.LocalSessionService;

import org.jdbi.v3.core.Jdbi;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RaffleAppApplicationModule extends AbstractModule
{
    public static void main(String[] args)
    {
        Base.initializeApp(args, new RaffleAppApplicationModule()).run();
    }


    @Override
    protected void configure()
    {
        bind(Application.class).to(RaffleAppApplication.class);

        bind(Authenticator.class).to(LocalAuthenticator.class);
        bind(SessionService.class).to(LocalSessionService.class);

        bind(DefaultDataBaseConfig.class).to(RaffleAppDefaultDataBaseConfig.class);
    }


    @Provides
    @Singleton
    public RaffleRepository raffleRepositoryProvider(Jdbi jdbi)
    {
        return jdbi.onDemand(RaffleRepository.class);
    }
}