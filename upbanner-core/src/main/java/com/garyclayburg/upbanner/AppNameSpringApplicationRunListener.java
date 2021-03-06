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
        log.debug("constructing AppNameSpringApplicationRunListener...");
        this.application = application;
        this.mainApplicationClassName = application.getMainApplicationClass().toString();
    }

    public String getMainApplicationClassName() {
        return mainApplicationClassName;
    }

    @Override
    public void starting() {
        log.debug("starting...");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        log.debug("environmentPrepared...");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.debug("contextPrepared...");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.debug("contextLoaded...");
        DebugDumper debugDumper = DebugDumper.getInstance(context);
        debugDumper.dumpAll();
    }

    public void started(ConfigurableApplicationContext context) {
        log.debug("started... "+ context.isActive());
        if (context.isActive()) {
            /*
             this may not be active when running under devtools AND app was restarted
             it could be inactive if Spring has already found some other issue that
             prevented app startup.  In this case we don't want to add to the confusion
             by trying to use our bean - the app isn't really up anyway.
             */
            try {
                String[] beanNamesForType = context.getBeanNamesForType(WhatsUpProbes.class);
                /*
                The WhatsUpProbes bean will not be loaded if the application using this starter has not
                enabled AutoConfiguration.  This would prevent loading ALL of our beans defined in
                spring.factories org.springframework.boot.autoconfigure.EnableAutoConfiguration key.
                In this case, we don't want to use this library anyway.  We especially don't
                want a getBean() to prevent app startup.
                 */
                if (beanNamesForType.length > 0) {
                    WhatsUpProbes whatsUpProb = context.getBean(WhatsUpProbes.class);
                    whatsUpProb.setApplicationName(mainApplicationClassName);
                    String[] whatsUpBeanNames = context.getBeanNamesForType(WhatsUpBanner.class);
                    if (whatsUpBeanNames.length > 0) {
                        WhatsUpBanner whatsUpBanner = context.getBean(WhatsUpBanner.class);
                        whatsUpBanner.printBanner();
                    }
                }
            } catch (IllegalStateException illegalStateException) {
                log.warn("Cannot print upbanner.  Are you using spring devtools?", illegalStateException);
            } catch (BeansException e) {
                log.warn("cannot find WhatsUp Bean");
            }
        }
    }

    public void running(ConfigurableApplicationContext context) {
        log.debug("running...");
    }

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
