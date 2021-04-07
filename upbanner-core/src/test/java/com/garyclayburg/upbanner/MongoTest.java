package com.garyclayburg.upbanner;

import static org.junit.Assert.*;

import com.mongodb.MongoClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2021-04-07 10:06
 *
 * @author Gary Clayburg
 */
public class MongoTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MongoTest.class);

    @Test
    public void ok() {

    }

    @Test
    public void testMaskMongoDBUriPassword() {
        assertEquals("mongodb://patonsynconsoleuser:xxxx@yale.garyclayburg.com:27017/patonsynconsoledb",
                MongoUpContributor.maskSecrets("mongodb://patonsynconsoleuser:monkey123@yale.garyclayburg.com:27017/patonsynconsoledb"));
        assertEquals("mongodb://mongodb0.example.com:27017",
                MongoUpContributor.maskSecrets("mongodb://mongodb0.example.com:27017"));
        assertEquals("mongodb://myDBReader:xxxx@mongodb0.example.com:27017/?authSource=admin\n",
                MongoUpContributor.maskSecrets("mongodb://myDBReader:D1fficultP%40ssw0rd@mongodb0.example.com:27017/?authSource=admin\n"));
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
