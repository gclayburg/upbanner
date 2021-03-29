package com.example.demo;

import javax.annotation.PostConstruct;

import com.garyclayburg.upbanner.WhatsUpBanner;
import com.garyclayburg.upbanner.WhatsUpProbes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2021-03-23 15:21
 *
 * @author Gary Clayburg
 */
@Component
public class MyWhatsUp implements WhatsUpBanner {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MyWhatsUp.class);
    private final WhatsUpProbes whatsUpProbes;

    public MyWhatsUp(WhatsUpProbes whatsUpProbes) {
        this.whatsUpProbes = whatsUpProbes;
    }

    @PostConstruct
    public void printDebugOnStartup() {
        whatsUpProbes.dumpAll();
    }

    @Override
    public void printBanner() {
        log.info("\n\napp is ready on port " + whatsUpProbes.getListeningPort() + "\n");
    }
}
