package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingClass({"org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent"})
public class PortListenerSpringBoot2 implements ApplicationListener<ServletWebServerInitializedEvent> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(PortListenerSpringBoot2.class);

    private final WhatsUp whatsUp;

    public PortListenerSpringBoot2(WhatsUp whatsUp) {
        this.whatsUp = whatsUp;
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        whatsUp.printVersion(event.getWebServer().getPort());
    }
}

