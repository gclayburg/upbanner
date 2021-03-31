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

@SpringBootTest(classes = {FooConfig.class}) //very minimal context under spring 2.x
public class JarOnly244applicationTests {

    public static final Logger log = LoggerFactory.getLogger(JarOnly244applicationTests.class);
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOutputStream = System.out;
    private static boolean capturingOutput = false;

    public JarOnly244applicationTests() {
    }

    @BeforeAll
    public static void beforeClass() {
        log.debug("running Jar1519ApplicationTests beforeClass");
        System.setOut(new PrintStream(outContent));
        capturingOutput = true;
    }

    //autowire simple bean
    @Autowired
    FooService fooService;

    @BeforeEach
    public void setUp() {
        log.info("settingup");
        if (capturingOutput) {
            System.setOut(originalOutputStream); // only capture initial server start
            capturingOutput = false;
            log.debug("captured output follows: ");
            log.info("showing output here");
            System.out.println(outContent);
        }
    }

    @Test
    public void upbannerApplicationIsUP() {
        assertTrue(outContent.toString().contains("Started "));
        log.info("test finished");
    }
}
