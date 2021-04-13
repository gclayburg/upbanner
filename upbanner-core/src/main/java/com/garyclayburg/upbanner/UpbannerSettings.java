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
     * if true, print entire Environment to the console during application startup.
     */
    private boolean debug = false;
    /**
     * If true, print defined upbanner to console on successful webapp startup.
     *
     */
    private boolean showBanner = true;

    public boolean isDebug() {
        return debug;
    }

    /**
     * If true, print entire Environment to the console during application startup.
     * @param debug If true, print entire Environment to the console during application startup.
     * @return UpbannerSettings
     */
    public UpbannerSettings setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isShowBanner() {
        return showBanner;
    }

    /**
     * If true, print defined upbanner to console on successful webapp startup.
     * <br><br>
     * If the app is not a web app, or the app never reaches the point at startup where it begins
     * to listen on a port, nothing will be printed.
     * @param showBanner If true, print defined upbanner to console on successful webapp startup.
     * @return UpbannerSettings
     */
    public UpbannerSettings setShowBanner(boolean showBanner) {
        this.showBanner = showBanner;
        return this;
    }
}
