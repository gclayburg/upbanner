package com.example.mongo244;

import static org.junit.jupiter.api.Assertions.*;

import com.garyclayburg.upbanner.WhatsUpProbes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class Mongo244ApplicationTests {

    static {
        System.setProperty("somejunk", "stilljunk");
        System.setProperty("someincorrectlyfilteredvalue", "${version}");
    }

    @Autowired
    WhatsUpProbes whatsUpProbes;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
    }

    @Test
    void getEnvironmentProp() {
        assertNotNull(whatsUpProbes.getEnvironmentPropertyPrintable("PWD"));
        assertNotNull(whatsUpProbes.getEnvironmentPropertyPrintable("somejunk"));
        assertEquals("", whatsUpProbes.getEnvironmentPropertyPrintable("thiskeyshouldnotexist"));
        assertEquals("", whatsUpProbes.getEnvironmentPropertyPrintable("someincorrectlyfilteredvalue"));

        assertNotNull(whatsUpProbes.getEnvironmentProperty("somejunk"));
        assertNull(whatsUpProbes.getEnvironmentProperty("thiskeyshouldnotexist"));
        assertNull(whatsUpProbes.getEnvironmentProperty("someincorrectlyfilteredvalue"));
    }

    @Test
    void contextok() {
        assertNotNull(context);
    }
}
