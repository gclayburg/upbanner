package com.garyclayburg.upbanner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints UP banner to stdout on Spring Boot application startup.  Build properties from
 * {@code META-INF/boot-info.properties} are used to show relevant info about this app.
 * <p>Example output:</p>
 * <pre>
 * 2019-03-01 09:10:55.485  INFO 14437 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application:1.0 is UP!
 *     Local:     http://localhost:8070
 *     External:  http://127.0.1.1:8070
 *     Host:      http://gary-XPS-13-9360:8070
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 *
 * <p>Example output with git.properties present in classpath:</p>
 *
 * <pre>
 * 2019-03-01 09:54:41.688  INFO 15686 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application:1.0 is UP!                        git.build.time:    2019-03-01T09:52:34-0700
 *     Local:     http://localhost:8070              git.build.version: 0.0.2-SNAPSHOT
 *     External:  http://127.0.1.1:8070              git.commit.id:     10db5b227f40569993c99ac8b6b5fd48860f6496
 *     Host:      http://gary-XPS-13-9360:8070       git.remote.origin: Unknown
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 *
 * @author Gary Clayburg
 */
public class WhatsUp implements WhatsUpBanner {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUp.class);
    private final WhatsUpProbes whatsUpProbes;

    public WhatsUp(WhatsUpProbes whatsUpProbes) {
        this.whatsUpProbes = whatsUpProbes;
    }

    /**
     * Prints the environment in which this app is running to the log system (console)
     * We attempt to print this information as soon as possible during application startup
     */
    @PostConstruct
    public void printDebugOnStartup() {
        whatsUpProbes.dumpAll();
    }

    @Override
    public void printBanner() {
        whatsUpProbes.printDefaultBanner();
    }
}
