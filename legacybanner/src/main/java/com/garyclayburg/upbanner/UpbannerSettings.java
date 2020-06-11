package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2020-06-05 18:24
 *
 * @author Gary Clayburg
 */
@Component
@ConfigurationProperties(prefix = "upbanner")
public class UpbannerSettings {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UpbannerSettings.class);

    /**
    print entire Environment to the console on successful startup
     */
    private boolean showEnv = false;
    /**
     * print defined banner on successful startup.
     *
     * The default example might look like this:
     *
     * <pre>
     * ----------------------------------------------------------------------------------------------------
     *     memuser:1.0 is UP!
     *     Local:     https://localhost:8443
     *     External:  https://127.0.1.1:8443
     *     Host:      https://gary-XPS-13-9360:8443
     * ----------------------------------------------------------------------------------------------------
     * </pre>
     */
    private boolean showBanner = true;

    public boolean isShowEnv() {
        return showEnv;
    }

    public UpbannerSettings setShowEnv(boolean showEnv) {
        this.showEnv = showEnv;
        return this;
    }

    public boolean isShowBanner() {
        return showBanner;
    }

    public UpbannerSettings setShowBanner(boolean showBanner) {
        this.showBanner = showBanner;
        return this;
    }
}
