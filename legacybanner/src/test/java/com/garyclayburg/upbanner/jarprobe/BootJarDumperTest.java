package com.garyclayburg.upbanner.jarprobe;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootJarDumperTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(BootJarDumperTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }

    @Test
    public void constructBootJarWar() {
        assertNotNull(BootJarDumper.createTrimmedPath(new StringBuilder(), "file:/doesnotexist/gclaybur/dev/gvsync/upbanner/webjar1519/target/webjar1519-2.1.2-SNAPSHOT.jar!/BOOT-INF/lib/legacybanner-2.1.2-SNAPSHOT.jar!/"));
    }

    @Test
    public void constructBootWar() {
        assertNotNull(BootJarDumper.createTrimmedPath(new StringBuilder(), "file:/doesnotexist/gclaybur/dev/gvsync/upbanner/weboshiwar244/target/weboshiwar244-2.1.2-SNAPSHOT.war!/WEB-INF/lib/legacybanner-2.1.2-SNAPSHOT.jar!/"));
    }
}
