package com.garyclayburg.upbanner;

import java.time.Duration;

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

    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        /*
        This variant of the started method signature seems to be new as of Spring boot 2.6
        Spring Boot 3.x seems to ONLY have this variant of started()

        So it's a little tricky to determine when our spring boot app is actually started
        Spring boot 1.x app:
        - spring boot calls finished(ConfigurableApplicationContext context, Throwable exception)
        Spring boot 2.0 - 2.5 app:
        - spring boot calls started(ConfigurableApplicationContext context)
        Spring boot 2.6
        - spring boot calls started(ConfigurableApplicationContext context, Duration timeTaken)  IF it exists, otherwise
        - spring boot calls started(ConfigurableApplicationContext context)
        Spring boot 3.0
        - spring boot calls started(ConfigurableApplicationContext context, Duration timeTaken)

        We want the latest version of upbanner-starter to be usable by any version of spring boot.
        We do not want to create separate versions of upbanner-starter for each version of spring boot.
         */
        log.debug("starting a Spring boot 2.6+ app");
        started(context);
    }
    public void started(ConfigurableApplicationContext context) {
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
        log.debug("starting Spring Boot 1.x app");
        started(context);
    }
}
