import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.garyclayburg.upbanner.WhatsUp;
import com.garyclayburg.upbanner.jarprobe.FileJarDumper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;

public class UpbannerApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(UpbannerApplicationTests.class);

    @Test
    public void contextLoads() {
    }

    @Test
    public void name() {
        String value = "2020-12-08T00:42:04Z";
        String updatedValue = String.valueOf(DateTimeFormatter.ISO_INSTANT
                .parse(value, Instant::from).toEpochMilli());
        log.info("updated: " + updatedValue);
    }

    @Test
    public void dumpBuildProps() {
        dumpBuildPropertiesWithTime("2020-12-08T00:52:19Z");
        dumpBuildPropertiesWithTime("");
        dumpBuildPropertiesWithTime("unparsable gibberish");
    }

    private void dumpBuildPropertiesWithTime(String timespec) {
        Properties p = new Properties();
        p.setProperty("time", timespec);
        BuildProperties buildProperties = new BuildProperties(p);
        WhatsUp whatsUp = new WhatsUp(null, buildProperties, null,null, new FileJarDumper());
        StringBuilder probeOut = new StringBuilder();
        whatsUp.dumpBuildProperties(probeOut);
        log.info(probeOut.toString());
    }

    @Test
    public void dumpCPU() {
        WhatsUp whatsUp = new WhatsUp(null, null, null,null,new FileJarDumper());
        StringBuilder probeOut = new StringBuilder();
        whatsUp.dumpCPUlimits(probeOut);
        log.info("\n" +probeOut.toString());
    }
}
