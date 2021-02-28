package com.garyclayburg.upbanner.jarprobe;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJarDumperTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FileJarDumperTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }


    @Test
    public void inspectClasspathEntryURL() throws MalformedURLException {
        FileJarDumper fileJarDumper = new FileJarDumper();
        StringBuilder probeOut = new StringBuilder();
        String fileName = fileJarDumper.inspectClasspathEntryURL(probeOut,
                new URL("jar:file:/home/springboot/app/BOOT-INF/lib/stepsapi-0.8.1-SNAPSHOT.jar!/"));
        assertEquals("/home/springboot/app/BOOT-INF/lib/stepsapi-0.8.1-SNAPSHOT.jar", fileName);
        assertFalse(probeOut.toString().contains("WARN"));
        log.info(probeOut.toString());
    }

    @Test
    public void inspectClasspathEntryURLTomcat() throws MalformedURLException {
        FileJarDumper fileJarDumper = new FileJarDumper();
        StringBuilder probeOut = new StringBuilder();
        String fileName = fileJarDumper.inspectClasspathEntryURL(probeOut,
                new URL("file:/home/springboot/app/BOOT-INF/lib/log4j-api-2.13.3.jar"));
        assertEquals("/home/springboot/app/BOOT-INF/lib/log4j-api-2.13.3.jar", fileName);
        assertFalse(probeOut.toString().contains("WARN"));
        log.info(probeOut.toString());
    }

    @Test
    public void inspectClasspathEntryURLUnknown() throws MalformedURLException {
        FileJarDumper fileJarDumper = new FileJarDumper();
        StringBuilder probeOut = new StringBuilder();
        String fileName = fileJarDumper.inspectClasspathEntryURL(probeOut,
                new URL("http:stuff"));
        assertNull(fileName);
        log.info(probeOut.toString());
        assertTrue(probeOut.toString().contains("WARN"));
    }
}
