package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
/*If this class exists, we are running under spring boot version > 2.0.
This means that EmbeddedServletContainerInitializedEvent will
not exist and would otherwise stop application startup.
*/
@ConditionalOnMissingClass({"org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent"})
public class PortListenerSpringBoot1 implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(PortListenerSpringBoot1.class);

    private final WhatsUp whatsUp;

    public PortListenerSpringBoot1(WhatsUp whatsUp) {
        this.whatsUp = whatsUp;
    }

    @Override
    public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
        int localPort = event.getEmbeddedServletContainer().getPort();
        whatsUp.printVersion(localPort);
    }
}
