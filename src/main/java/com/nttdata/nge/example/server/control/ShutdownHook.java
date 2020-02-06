package com.nttdata.nge.example.server.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles the JVM, SpringBoot Application Shutdown gracefully
 * 
 * Based on the implementation from: https://github.com/ramato-procon/springboot-graceful-terminator
 *
 */
@Component
@Lazy
public class ShutdownHook implements Runnable {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private final List<WebServer> embeddedContainers = new ArrayList<>();
    private ConfigurableApplicationContext applicationContext;
    
    /*
     * Set to true in run(), set to false in init(). Checked in run() for multiple executions during shutdown. Should
     * also fix issues with parallel calls (synchronizing access seems like overkill).
     */
    private volatile boolean alreadyExecuted = false;
    
    private int shutdownTimeout = 60000;
    
    @Autowired
    private ShutdownHttpFilter filter;
    
    @Autowired
    private Optional<IWebsocketSessions> sessions;
    
    
    /**
     * Initializes the Application Context
     * 
     * @param applicationContext
     *            the application context
     */
    public void init(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.alreadyExecuted = false;
    }
    
    /**
     * Added as hook so that it is called when the application is being shutdown (e.g. by CTRL+C).
     */
    public void run() {

        if (alreadyExecuted) {
            log.info("Shutdown - was already executed. Skipping.");
            return;
        }
        log.info("Shutdown - started. Existing HTTP sessions and scheduled jobs can finish within: " + shutdownTimeout
                + " miliseconds.");
        ExecutorService shutdownExecutor = null;
        try {
            // Shutdown HTTP
            shutdownExecutor = Executors.newSingleThreadExecutor();
            Future<Void> httpFuture = shutdownExecutor.submit(() -> {
                shutdownHttpFilter();
                stopWebsocketSessions();
                shutdownHTTPConnector();
                return null;
            });
            try {
                httpFuture.get(shutdownTimeout, TimeUnit.MILLISECONDS);
            } catch ( ExecutionException | TimeoutException e) {
                log.warn("HTTP graceful shutdown failed", e);
            } catch (InterruptedException interrupted) {
               Thread.currentThread().interrupt();
               log.warn("HTTP graceful shutdown failed", interrupted);
            }
            
        } finally {
            this.alreadyExecuted = true;
            shutdownExecutor(shutdownExecutor);
            shutdownApplication();
        }
    }
    
    private void stopWebsocketSessions() {
    	if (sessions.isPresent()) {
    		log.info("Shutdown - Websocket sessions");
    		sessions.get().close();
    	}
    }
    
    private void shutdownHttpFilter() {
        log.info("Shutdown - HTTP filter.");
        try {
            filter.shutdown();
        } catch (InterruptedException e) {
            log.warn("Shutdown - shutdownHttpFilter failed", e);
            Thread.currentThread().interrupt();
        }
    }
    
    private void shutdownHTTPConnector() {
        log.info("Shutdown - HTTP Connector.");
        try {
            for (WebServer embeddedServletContainer : embeddedContainers) {
                embeddedServletContainer.stop();
            }
        } catch (WebServerException e) {
            log.warn("Shutdown - shutdownHTTPConnector failed", e);
        }
    }
    
    private void shutdownExecutor(ExecutorService shutdownExecutor) {
        if (shutdownExecutor != null) {
            try {
                shutdownExecutor.shutdownNow();
            } catch (SecurityException e) {
                log.warn("Executor shutdown failed", e);
            }
        }
    }
    
    private void shutdownApplication() {
        log.info("Shutdown - ApplicationContext");
        applicationContext.close();
    }
    
    /**
     * On container initialized
     * 
     * @param event
     *            the embedded servlet container initialized event
     */
    @EventListener
    public synchronized void onContainerInitialized(WebServerInitializedEvent event) {
        embeddedContainers.add(event.getSource());
    }
    
}