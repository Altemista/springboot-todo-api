package com.nttdata.nge.example.server.control;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Creates a SpringBoot Application and enables the graceful shutdown hooks
 */
public class StartupWrapper extends SpringApplication {
    
    /**
     * Constructor
     * 
     * @param sources
     *            the sources
     */
    public StartupWrapper(Class<?>... primarySources) {
        super(primarySources);
    }
    
    /**
     * Creates a new SpringBoot Application instance and registers the GracefulShutdownHook
     * 
     * @param appClazz
     *            the wrapped class
     * @param bannerModeOff
     *            if true set bannerMode for app to OFF
     * @param args
     *            arguments as passed through
     * @return the application context
     */
    public static ConfigurableApplicationContext run(Class<?> appClazz, boolean bannerModeOff, String... args) {
        
        StartupWrapper app = new StartupWrapper(appClazz);
        if (bannerModeOff) {
            app.setBannerMode(Mode.OFF);
        }
        ConfigurableApplicationContext applicationContext = app.run(args);
        ShutdownHook hook = applicationContext.getBean(ShutdownHook.class);
        Runtime.getRuntime().addShutdownHook(new Thread(hook));
        
        return applicationContext;
    }
    
    /**
     * We extend the Spring Application to catch all run calls. This is important for the restart of the application as
     * the shutdown hook handling is skipped there and a new application context is not correctly linked to the hook.
     */
    @Override
    public ConfigurableApplicationContext run(String... args) {
        this.setRegisterShutdownHook(false);
        ConfigurableApplicationContext applicationContext = super.run(args);
        ShutdownHook hook = applicationContext.getBean(ShutdownHook.class);
        hook.init(applicationContext);
        applicationContext.addApplicationListener(event -> {
            /**
             * Add an listener for the ContextClosedEvent. This event is not raised during the normal shutdown but e.g.
             * during startup. We want to close the schedulers etc. gracefully in this case as well.
             */
            if (event instanceof ContextClosedEvent) {
                applicationContext.getBean(ShutdownHook.class).run();
            }
        });
        
        return applicationContext;
    }
    
}