package com.example.demo;

import com.garyclayburg.upbanner.AbstractWhatsUp;
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
public class MyWhatsUp extends AbstractWhatsUp {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MyWhatsUp.class);

    @Override
    public void printVersion(int localPort) {
        log.info("\n\napp is ready on port " + localPort+"\n");
    }
}
