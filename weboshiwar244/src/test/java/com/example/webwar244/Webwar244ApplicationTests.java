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

    static {
        System.setProperty("PS1_VALID_IN_BASH_BUT_UNPARSABLE_BY_SPRING_ENVIRONMENT", "\\[\\e]0;\\u@\\h:\\w\\a\\]\\n$(last_stat=$?;if [ $last_stat -ne 0 ]; then echo \"\\e[41;1;33m${last_stat}\\e[0m \"; fi)\\e[$(( (${EUID}==0) ? 31 : 32))m\\! \\j [\\D{%m-%d} \\t] RSA ${DISPLAY:+$DISPLAY }\\u@\\e[31;1;43m\\h\\e[0m Dell Inc. $(declare -F __git_ps1 &>/dev/null && __git_ps1 \"[%s]\")\\e[0;33m $(if [ -n ${BASH_VERSION} ]; then echo ${DIRSTACK[0]}; else echo $PWD; fi) \\e[0;32m$(if [ -n ${BASH_VERSION} ]; then echo ${DIRSTACK[@]:1}; else echo \"\"; fi)\\e[0m\\n\\$\n");
    }
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
