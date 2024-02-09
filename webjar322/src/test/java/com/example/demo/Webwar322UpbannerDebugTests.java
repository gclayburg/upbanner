package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"upbanner.debug=true"})
class Webwar322UpbannerDebugTests {

    private static final Logger log = LoggerFactory.getLogger(Webwar322UpbannerDebugTests.class);

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOutputStream = System.out;
    private static boolean capturingOutput = false;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
    }

    @BeforeAll
    static void beforeAll() {
        log.debug("running Webwar244ApplicationTests beforeAll");
        System.setOut(new PrintStream(outContent));
        capturingOutput = true;
    }

    @BeforeEach
    void setUp() {
        log.debug("running setup()");
        if (capturingOutput) {
            System.setOut(originalOutputStream); // only capture initial server start
            capturingOutput = false;
            log.debug("captured output follows: ");
            System.out.println(outContent);
        }
    }

    @Test
    void upbannerisupwithoshiprobe() {
        assertTrue(outContent.toString().contains("App322 is UP!"));
    }

    @Test
    void noWARNmessages() {
        assertFalse(outContent.toString().contains("WARN"));
    }

    @Test
    void lookupnonexistantBean() {
        assertNotNull(context);
    }
}
