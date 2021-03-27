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

    @Test
    public void snapshotJar() {
        FileJarDumper fileJarDumper = new FileJarDumper();
        assertTrue(fileJarDumper.shouldShowManifest("/home/gclaybur/.gradle/caches/modules-2/files-2.1/com.garyclayburg/upbanner-starter/2.1.2-SNAPSHOT/9b356f3d0228e37db85d38f64a585f2f9fb5b6c5/upbanner-starter-2.1.2-SNAPSHOT.jar"));
        assertTrue(fileJarDumper.shouldShowManifest("/home/gclaybur/.m2/repository/com/garyclayburg/upbanner-starter/2.1.2-SNAPSHOT/upbanner-starter-2.1.2-20210326.185920-25.jar"));
        assertFalse(fileJarDumper.shouldShowManifest(""));
        assertFalse(fileJarDumper.shouldShowManifest(null));
        assertFalse(fileJarDumper.shouldShowManifest("/home/gclaybur/.m2/repository/org/springframework/spring-jcl/5.3.5/spring-jcl-5.3.5.jar"));
    }
}
