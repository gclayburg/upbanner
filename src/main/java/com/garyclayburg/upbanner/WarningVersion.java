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
@ConditionalOnMissingClass({"org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent"})
public class WarningVersion {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WarningVersion.class);

    public WarningVersion() {
        log.warn("This version of WhatsUp only works with Spring Boot 2.x.  This app uses Spring Boot 1.0+.  Please change WhatsUp to version 1.0.0.");
        /* won't even be visible unless someone disables the nested dependency like this - but hey, pigs could fly.
            compile(group: 'com.garyclayburg', name:'upbanner', version: '2.0.0'){
              exclude(group: 'org.springframework.boot', module: 'spring-boot-starter-web')
            }
         */

    }
}
