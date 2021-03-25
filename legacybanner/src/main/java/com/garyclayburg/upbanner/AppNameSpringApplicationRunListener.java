package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <br><br>
 * Created 2021-03-23 16:00
 *
 * @author Gary Clayburg
 */
public class AppNameSpringApplicationRunListener implements SpringApplicationRunListener {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AppNameSpringApplicationRunListener.class);
    private final SpringApplication application;
    private final String mainApplicationClassName;

    public AppNameSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.mainApplicationClassName = application.getMainApplicationClass().toString();
    }

    public String getMainApplicationClassName() {
        return mainApplicationClassName;
    }

    @Override
    public void starting() {
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

//    @Override
    public void started(ConfigurableApplicationContext context) {
        try {
            AbstractWhatsUp whatsUp = context.getBean(AbstractWhatsUp.class);
            whatsUp.setApplicationName(mainApplicationClassName);
            whatsUp.printBanner();
        } catch (BeansException e) {
            log.warn("cannot find WhatsUp Bean", e);
        }
    }

//    @Override
    public void running(ConfigurableApplicationContext context) {
    }

//    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        /*
        this is a little sketchy.  This finished() method exists in the SpringApplicationRunListener
        interface used by spring boot 1.x.  It does not exist under spring boot 2.x
        But we have it here because we are including this class in the app and it may be running
        under spring boot 1.x or 2.x
         */
        started(context);
    }
}
