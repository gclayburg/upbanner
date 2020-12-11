package com.garyclayburg.upbanner.oshiprobe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2020-12-10 17:57
 *
 * @author Gary Clayburg
 */
public class EmptyOshiProbe extends OshiProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(EmptyOshiProbe.class);

    @Override
    public void createReport(StringBuilder stringBuilder) {

    }
}
