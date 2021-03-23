package com.example.demo;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebJar1519ApplicationTests {

    public static final Logger log = LoggerFactory.getLogger(WebJar1519ApplicationTests.class);
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOutputStream = System.out;
    private static boolean capturingOutput = false;

    @LocalServerPort
    private int randomServerPort;

    public WebJar1519ApplicationTests() {
    }

    @BeforeClass
    public static void beforeClass() {
        log.debug("running Webwar244ApplicationTests beforeClass");
        System.setOut(new PrintStream(outContent));
        capturingOutput = true;
    }

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
        if (capturingOutput) {
            System.setOut(originalOutputStream); // only capture initial server start
            capturingOutput = false;
            log.debug("captured output follows: ");
            System.out.println(outContent);
        }
    }

    @Test
    public void upbannerApplicationIsUP() {
        log.info("port is " + randomServerPort);
        assertTrue(outContent.toString().contains("Application is UP!"));
    }
}
