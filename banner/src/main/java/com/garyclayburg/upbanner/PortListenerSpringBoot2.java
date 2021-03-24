package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * Trigger display of upbanner after the listening port is ready.  This class
 * is used for applications using Spring Boot 2.x only.
 */
@Component
/*
we can't necessarily rely on how the user of this library has configured @ComponentScan
 to pick up WhatsUpConfig, so we import it here.
 WhatsUpConfig will only create WhatsUp if the user of this library has not defined their
 own implementation of AbstractWhatsUp
 */
@Import(value = {
        WhatsUpConfig.class,
        ContainerVMEnvProbe.class,
        UpbannerSettings.class})
@ConditionalOnMissingClass({"org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent"})
public class PortListenerSpringBoot2 implements ApplicationListener<ServletWebServerInitializedEvent> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(PortListenerSpringBoot2.class);

    private final AbstractWhatsUp whatsUp;

    public PortListenerSpringBoot2(AbstractWhatsUp whatsUp) {
        this.whatsUp = whatsUp;
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        whatsUp.setListeningPort(event.getWebServer().getPort());
    }
}

