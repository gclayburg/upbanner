package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebJar27Tests {

    private static final Logger log = LoggerFactory.getLogger(WebJar27Tests.class);

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOutputStream = System.out;
    private static boolean capturingOutput = false;

    @Test
    void contextLoads() {
    }

    @BeforeAll
    static void beforeAll() {
        log.debug("running beforeAll");
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
    void upbannerisup() {
        assertTrue(outContent.toString().contains("WebJar27Tests is UP!"));
    }
}
