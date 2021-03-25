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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FooConfig.class}) //very minimal context initialization, i.e. no spring.factories bean processing
public class JarOnly15applicationTests {

    public static final Logger log = LoggerFactory.getLogger(JarOnly15applicationTests.class);
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOutputStream = System.out;
    private static boolean capturingOutput = false;

    public JarOnly15applicationTests() {
    }

    @BeforeClass
    public static void beforeClass() {
        log.debug("running Jar1519ApplicationTests beforeClass");
        System.setOut(new PrintStream(outContent));
        capturingOutput = true;
    }

    //autowire simple bean
    @Autowired
    FooService fooService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.info("settingup");
        log.debug("Running test setUp: " + testName.getMethodName());
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
        log.info("log me please");
    }
}
