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

import com.google.inject.Singleton;

import net.talpidae.base.server.Server;
import net.talpidae.base.server.ServerConfig;
import net.talpidae.base.util.Application;
import net.talpidae.base.util.log.LoggingConfigurer;
import net.talpidae.raffle.app.util.server.RaffleRootHandlerWrapper;

import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.servlet.ServletException;

import io.undertow.server.HandlerWrapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.lang.System.exit;
import static net.talpidae.base.util.log.LoggingConfigurer.CONTEXT_INSECT_NAME_KEY;


@Singleton
@Slf4j
public class RaffleAppApplication implements Application
{
    private final ServerConfig serverConfig;

    private final Server server;

    private final HandlerWrapper handlerWrapper;

    private final LoggingConfigurer loggingConfigurer;


    @Inject
    public RaffleAppApplication(ServerConfig serverConfig, Server server, RaffleRootHandlerWrapper handlerWrapper, LoggingConfigurer loggingConfigurer)
    {
        this.serverConfig = serverConfig;
        this.server = server;
        this.handlerWrapper = handlerWrapper;
        this.loggingConfigurer = loggingConfigurer;
    }


    @Override
    public void run()
    {
        serverConfig.setRootHandlerWrapper(handlerWrapper);

        loggingConfigurer.putContext(CONTEXT_INSECT_NAME_KEY, "raffle-app");
        try
        {
            server.start();

            val bindAddress = new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort());
            log.info("server started on {}", bindAddress.toString());

            server.waitForShutdown();
        }
        catch (ServletException e)
        {
            log.error("failed to start server: {}", e.getMessage());
            exit(1);
        }
    }
}