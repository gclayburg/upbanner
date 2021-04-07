package com.garyclayburg.upbanner;

import static org.junit.jupiter.api.Assertions.*;

import com.example.mongo244.Mongo244Application;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


/**
 * <br><br>
 * Created 2021-04-07 09:07
 *
 * @author Gary Clayburg
 */
@SpringBootTest(classes = {Mongo244Application.class})
public class MongoBannerSpringTests {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MongoBannerSpringTests.class);

    @Autowired
    WhatsUpProbes whatsUpProbes;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextok() {
        assertNotNull(context);
    }

    @Test
    void mongoprobe() {
        MongoUpContributor mongoUpContributor = new MongoUpContributor(whatsUpProbes, context);
        StringBuilder probe = new StringBuilder();
        mongoUpContributor.call(probe);
        assertTrue(probe.toString().contains("embedded mongodb"));
    }

    @Test
    void callprobewithmongo() {
        whatsUpProbes.registerUpContributor(new MongoUpContributor(whatsUpProbes, context));
        StringBuilder stringBuilder1 = whatsUpProbes.buildBanner(stringBuilder -> {
        });
        log.info("banner: " + stringBuilder1.toString());
        assertTrue(stringBuilder1.toString().contains("embedded mongodb"));
        String banner = whatsUpProbes.buildBanner().toString();
        assertTrue(banner.contains("embedded mongodb"));
        log.info("banner is: " + banner);
    }
/*
    @Test
    public void testMongoClient() {
        MongoClient mongoClient = new MongoClient();
        StringBuilder stringBuilder = new StringBuilder();
        MongoUpContributor.createMongoClientLine(stringBuilder,mongoClient);
        log.info("probed mongo client: " + stringBuilder.toString());
        assertTrue(stringBuilder.toString().contains("uses"));
    }
*/
}
