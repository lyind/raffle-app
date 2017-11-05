package net.talpidae.raffle.app.util.server;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceHandler;


@Singleton
public class RaffleRootHandlerWrapper implements HandlerWrapper
{
    private final FallbackFileSystemResourceHandler fallbackFileSystemResourceHandler;

    private final RaffleClassPathResourceManager classPathResourceManager;

    private final RaffleFileSystemResourceManager fileSystemResourceManager;


    @Inject
    public RaffleRootHandlerWrapper(RaffleFileSystemResourceManager fileSystemResourceManager, RaffleClassPathResourceManager classPathResourceManager, FallbackFileSystemResourceHandler fallbackFileSystemResourceHandler)
    {
        this.fileSystemResourceManager = fileSystemResourceManager;
        this.classPathResourceManager = classPathResourceManager;
        this.fallbackFileSystemResourceHandler = fallbackFileSystemResourceHandler;
    }


    @Override
    public HttpHandler wrap(HttpHandler handler)
    {
        // 1. try to serve from FS working directory
        // 2. try to serve from class-path
        // 3. try to serve index.html from working directory
        // 4. 403
        return new ResourceHandler(fileSystemResourceManager, new ResourceHandler(classPathResourceManager, fallbackFileSystemResourceHandler));
    }
}