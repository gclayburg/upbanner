package com.garyclayburg.upbanner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.garyclayburg.upbanner.jarprobe.JarProbe;
import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

/**
 * Prints UP banner to stdout on Spring Boot application startup.  Build properties from
 * {@code META-INF/boot-info.properties} are used to show relevant info about this app.
 * <p>Example output:</p>
 * <pre>
 * 2019-03-01 09:10:55.485  INFO 14437 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application:1.0 is UP!
 *     Local:     http://localhost:8070
 *     External:  http://127.0.1.1:8070
 *     Host:      http://gary-XPS-13-9360:8070
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 *
 * <p>Example output with git.properties present in classpath:</p>
 *
 * <pre>
 * 2019-03-01 09:54:41.688  INFO 15686 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application:1.0 is UP!                        git.build.time:    2019-03-01T09:52:34-0700
 *     Local:     http://localhost:8070              git.build.version: 0.0.2-SNAPSHOT
 *     External:  http://127.0.1.1:8070              git.commit.id:     10db5b227f40569993c99ac8b6b5fd48860f6496
 *     Host:      http://gary-XPS-13-9360:8070       git.remote.origin: Unknown
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 *
 * @author Gary Clayburg
 */
@EnableConfigurationProperties(UpbannerSettings.class)
public class WhatsUp extends AbstractWhatsUp {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUp.class);
    private final UpbannerSettings upbannerSettings;
    public static final String CGROUP_FILE = "/proc/self/cgroup";

    public WhatsUp(Environment environment, BuildProperties buildProperties, UpbannerSettings upbannerSettings, OshiProbe oshiProbe, JarProbe jarProbe) {
        super(environment, buildProperties, oshiProbe,jarProbe);
        this.upbannerSettings = upbannerSettings;

    }

    /**
     * Prints the environment in which this app is running to the log system (console)
     * We attempt to print this information as soon as possible during application startup
     */
    @PostConstruct
    public void printDebugOnStartup() {
        if (upbannerSettings.isDebug()) {
            dumpAll();
        }
    }

    @Override
    public void printVersion(int localPort) {
        String hostAddress;
        String hostName;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostAddress = "unknown host";
            hostName = "unknown host";
        }

        Properties gitProperties = loadGitProperties();

        String proto = deduceProtocol();
        String banner = "\n----------------------------------------------------------------------------------------------------\n";
        String c1r1 = String.format("%s is UP!", deduceAppNameVersion());
        String c1r2 = String.format("Local:      %s://localhost:%s", proto, localPort);
        String c1r3 = String.format("External:   %s://%s:%s ", proto, hostAddress, localPort);
        String c1r4 = String.format("Host:       %s://%s:%s ", proto, hostName, localPort);
        if (isDocker()) {
            c1r4 = String.format("Docker:     %s://%s:%s ", proto, hostName, localPort);
        } else if (isKubernetes()) {
            c1r4 = String.format("Kubernetes: %s://%s:%s ", proto, hostName, localPort);
        }
        String c2r1 = String.format("git.commit.time:   %s", gitProperties.getProperty("git.commit.time"));
        String c2r2 = String.format("git.build.version: %s", gitProperties.getProperty("git.build.version"));
        String c2r3 = String.format("git.commit.id:     %s", gitProperties.getProperty("git.commit.id"));
        String c2r4 = String.format("git.remote.origin: %s", gitProperties.getProperty("git.remote.origin.url"));
        String c3r1 = String.format("build-date: %s", getBuildProp("org.label-schema.build-date"));
        String c3r2 = String.format("vcs-ref: %s", getBuildProp("org.label-schema.vcs-ref"));
        String c3r3 = String.format("vcs-url: %s", getBuildProp("org.label-schema.vcs-url"));
        String c3r4 = String.format("description: %s", getBuildProp("org.label-schema.description"));

        if (getBuildProp("org.label-schema.build-date").equals("development") ||
            getBuildProp("org.label-schema.build-date").equals("unknown")) {
            //skip showing the org.label.* values if they have not been generated by the build
            if (gitProperties.size() == 0) {
                banner += String.format("    %-45s\n", c1r1);
                banner += String.format("    %-45s\n", c1r2);
                banner += String.format("    %-45s\n", c1r3);
                banner += String.format("    %-45s\n", c1r4);
            } else {
                banner += String.format("    %-45s %s\n", c1r1, c2r1);
                banner += String.format("    %-45s %s\n", c1r2, c2r2);
                banner += String.format("    %-45s %s\n", c1r3, c2r3);
                banner += String.format("    %-45s %s\n", c1r4, c2r4);
            }
        } else {
            banner += String.format("    %-45s %-60s %s\n", c1r1, c2r1, c3r1);
            banner += String.format("    %-45s %-60s %s\n", c1r2, c2r2, c3r2);
            banner += String.format("    %-45s %-60s %s\n", c1r3, c2r3, c3r3);
            banner += String.format("    %-45s %-60s %s\n", c1r4, c2r4, c3r4);
        }
        banner += "----------------------------------------------------------------------------------------------------";
        if (upbannerSettings.isShowBanner()) {
            log.info(banner);
        }
    }

    private boolean isDocker() {
        boolean isDocker = false;
        try {
            isDocker = Files.lines(Paths.get(CGROUP_FILE)).map(line ->
                    line.matches(".*/docker.*")).reduce(false, (first, found) -> found || first);
        } catch (IOException e) {
            // not linux?
        }
        return isDocker;
    }

    private boolean isKubernetes() {
        boolean isKubernetes = false;
        try {
            isKubernetes = Files.lines(Paths.get(CGROUP_FILE)).map(line ->
                    line.matches(".*/kubepod.*")).reduce(false, (first, found) -> found || first);
        } catch (IOException e) {
            // not linux?
        }
        return isKubernetes;
    }
}
