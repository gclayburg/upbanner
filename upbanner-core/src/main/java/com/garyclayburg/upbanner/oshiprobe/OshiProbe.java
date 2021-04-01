package com.garyclayburg.upbanner.oshiprobe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2020-12-07 11:56
 *
 * @author Gary Clayburg
 */
public abstract class OshiProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(OshiProbe.class);

    public abstract void createReport(StringBuilder stringBuilder);
}
