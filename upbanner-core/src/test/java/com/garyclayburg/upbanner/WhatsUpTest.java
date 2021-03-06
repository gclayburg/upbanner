package com.garyclayburg.upbanner;

import static org.junit.Assert.*;

import java.util.Properties;

import com.garyclayburg.upbanner.jarprobe.FileJarDumper;
import com.garyclayburg.upbanner.oshiprobe.FullOshiProbe;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;

public class WhatsUpTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUpTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }


    @Test
    public void dumpMemory() {
        WhatsUpProbes whatsUpProbes = new WhatsUpProbes(null, null, null, null, null,null);
        StringBuilder probeOut = new StringBuilder();
        whatsUpProbes.dumpMemoryLimits(probeOut);
        log.info(probeOut.toString());
    }

    @Test
    public void dumpOShi() {
        UpbannerSettings upbannerSettings = new UpbannerSettings();
        upbannerSettings.setDebug(true);
        FullOshiProbe fullOshiProbe = new FullOshiProbe();
        StringBuilder probeOut = new StringBuilder();
        fullOshiProbe.createReport(probeOut);
        log.info("osh probe: " + probeOut);
    }

    @Test
    public void dumpBuildProps() {
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUpProbes whatsUpProbes = new WhatsUpProbes(null,buildProperties,new FullOshiProbe(),new FileJarDumper(),new UpbannerSettings(),null);
        StringBuilder probeOut = new StringBuilder();
        whatsUpProbes.dumpBuildProperties(probeOut);
        log.info(probeOut.toString());
    }

    @Test
    public void showName() {
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUpProbes whatsUpProbes = new WhatsUpProbes(null,buildProperties,new FullOshiProbe(),new FileJarDumper(),new UpbannerSettings(), null);
        assertEquals("MyMain", whatsUpProbes.convertStartClass("com.something.MyMain"));
        assertEquals("MyMain", whatsUpProbes.convertStartClass("something.MyMain"));
        assertEquals("MyMain", whatsUpProbes.convertStartClass("MyMain"));
        assertEquals("", whatsUpProbes.convertStartClass(""));
        assertNull(whatsUpProbes.convertStartClass(null));
    }

    @Test
    public void showNameFromSunJavaCommand() {
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUpProbes whatsUpProbes = new WhatsUpProbes(null,buildProperties,new FullOshiProbe(),new FileJarDumper(),new UpbannerSettings(),null);
        assertEquals("MyMain",
                whatsUpProbes.convertSunJavaCommand("com.garyclayburg.upbannerdemo.MyMain --server.port=8881"));
        assertEquals("MyMain",
                whatsUpProbes.convertSunJavaCommand("com.garyclayburg.upbannerdemo.MyMain --server.port=8881 --someoption=false"));
        assertEquals("MyMain",
                whatsUpProbes.convertSunJavaCommand("com.garyclayburg.upbannerdemo.MyMain"));
        assertEquals("JUnitStarter",
                whatsUpProbes.getMainStart("com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 com.example.demo.DemoApplicationTests"));
        assertEquals("GradleWorkerMain",
                whatsUpProbes.getMainStart("worker.org.gradle.process.internal.worker.GradleWorkerMain 'Gradle Test Executor 2'"));
        assertEquals("Application",
                whatsUpProbes.getMainStart("/home/gclaybur/dev/gvsync/upbanner/webjar244/target/surefire/surefirebooter2001082309298668663.jar /home/gclaybur/dev/gvsync/upbanner/webjar244/target/surefire 2021-03-22T08-45-40_408-jvmRun1 surefire5666214610931227172tmp surefire_04846830553387518722tmp"));
    }

    @Test
    public void Synconsole(){
        Properties p = new Properties();
        p.setProperty("time", "2020-12-08T00:52:19Z");
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUpProbes whatsUpProbes = new WhatsUpProbes(null,buildProperties,new FullOshiProbe(),new FileJarDumper(),new UpbannerSettings(),null);
        assertEquals("SynconsoleApp",
                whatsUpProbes.getMainStart("com.garyclayburg.synconsole.SynconsoleApp"));
    }

    @Test
    public void dumpCP() {
        FileJarDumper fileJarDumper = new FileJarDumper();
        StringBuilder out = new StringBuilder("\n");
        fileJarDumper.createSnapshotJarReport(out);
        log.info(out.toString());
    }
}
