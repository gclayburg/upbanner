package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2018-03-11 14:35
 *
 * @author Gary Clayburg
 */
@Component
@ConditionalOnMissingClass({"org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent"})
public class WarningVersion {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WarningVersion.class);

    public WarningVersion() {
        log.warn("This version of WhatsUp only works with Spring Boot 1.x.  This app uses Spring Boot 2.0+.  Please upgrade WhatsUp to version 2.0.0+.");
    }
}
