package com.garyclayburg.upbanner;

/**
 * <br><br>
 * Created 2021-03-28 14:11
 *
 * @author Gary Clayburg
 */
public interface WhatsUpBanner {

    /**
     * This method is called after a spring boot application has started.  If it is also a web app, it will be already
     * listening on a port at this time.
     */
    void printBanner();
}
