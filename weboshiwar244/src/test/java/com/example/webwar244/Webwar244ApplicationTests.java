package com.example.webwar244;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"upbanner.debug=true"})
class Webwar244ApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(Webwar244ApplicationTests.class);

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
        assertTrue(outContent.toString().contains("CustomAppNameHere is UP!"));
        assertTrue(outContent.toString().contains("Host System"));
        assertTrue(outContent.toString().contains("ProcessorID"));
    }

    @Test
    void lookupnonexistantBean() {
        assertNotNull(context);
//        context.getBeanNamesForType(MongoClient.class);
//        context.getBeanNamesForType(MongoClient.class);
    }
}
