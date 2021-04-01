package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * Trigger display of upbanner after the listening port is ready.  This class
 * is used for applications using Spring Boot 1.x only.
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
/*If this class exists, we are running under spring boot version > 2.0.
This means that EmbeddedServletContainerInitializedEvent will
not exist and would otherwise stop application startup.
*/
@ConditionalOnMissingClass({"org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent"})
public class PortListenerSpringBoot1 implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(PortListenerSpringBoot1.class);

    private final WhatsUpProbes whatsUp;

    public PortListenerSpringBoot1(WhatsUpProbes whatsUp) {
        this.whatsUp = whatsUp;
    }

    @Override
    public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
        int localPort = event.getEmbeddedServletContainer().getPort();
        whatsUp.setListeningPort(localPort);
    }
}
