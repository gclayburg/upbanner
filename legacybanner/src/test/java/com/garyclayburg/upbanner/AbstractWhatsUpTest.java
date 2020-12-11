package com.garyclayburg.upbanner;

import java.util.Properties;

import com.garyclayburg.upbanner.oshiprobe.FullOshiProbe;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;

public class AbstractWhatsUpTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AbstractWhatsUpTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }


    @Test
    public void dumpMemory() {
        WhatsUp whatsUp = new WhatsUp(null, null, null, null);
        StringBuilder probeOut = new StringBuilder();
        whatsUp.dumpMemory(probeOut);
        log.info(probeOut.toString());
    }

    @Test
    public void dumpOShi() {
        UpbannerSettings upbannerSettings = new UpbannerSettings();
        upbannerSettings.setDebug(true);
        FullOshiProbe fullOshiProbe = new FullOshiProbe();
        fullOshiProbe.createReport(new StringBuilder());
    }

    @Test
    public void dumpBuildProps() {
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUp whatsUp = new WhatsUp(null, buildProperties, null, null);
        StringBuilder probeOut = new StringBuilder();
        whatsUp.dumpBuildProperties(probeOut);
        log.info(probeOut.toString());
    }

    @Test
    public void dumpAll() {
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUp whatsUp = new WhatsUp(null, buildProperties, null, new FullOshiProbe());
        whatsUp.dumpAll();
    }
}
