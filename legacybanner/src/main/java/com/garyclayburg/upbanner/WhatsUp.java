package com.garyclayburg.upbanner;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Prints UP banner to stdout on Spring Boot application startup.  Build properties from
 * {@code META-INF/boot-info.properties} are used to show relevant info about this app.
 * <p>Example output:<p>
 * <pre>
 * 2019-03-01 09:10:55.485  INFO 14437 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application is UP!                            git.build.time:    null
 *     Local:     http://localhost:8070              git.build.version: null
 *     External:  http://127.0.1.1:8070              git.commit.id:     null
 *     Host:      http://gary-XPS-13-9360:8070       git.remote.origin: null
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 *
 * Example output with git.properties present in classpath:<p>
 *
 * <pre>
 * 2019-03-01 09:54:41.688  INFO 15686 --- [           main] com.garyclayburg.upbanner.WhatsUp        :
 * ----------------------------------------------------------------------------------------------------
 *     Application is UP!                            git.build.time:    2019-03-01T09:52:34-0700
 *     Local:     http://localhost:8070              git.build.version: 0.0.2-SNAPSHOT
 *     External:  http://127.0.1.1:8070              git.commit.id:     10db5b227f40569993c99ac8b6b5fd48860f6496
 *     Host:      http://gary-XPS-13-9360:8070       git.remote.origin: Unknown
 * ----------------------------------------------------------------------------------------------------
 * </pre>
 * @author Gary Clayburg
 */
@Component
public class WhatsUp {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUp.class);

    private final Environment environment;
    private final BuildProperties buildProperties;

    public WhatsUp(Environment environment, BuildProperties buildProperties) {
        this.environment = environment;
        this.buildProperties = buildProperties;
    }

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
        String c1r2 = String.format("Local:     %s://localhost:%s", proto, localPort);
        String c1r3 = String.format("External:  %s://%s:%s ", proto, hostAddress, localPort);
        String c1r4 = String.format("Host:      %s://%s:%s ", proto, hostName, localPort);
        String c2r1 = String.format("git.build.time:    %s",gitProperties.getProperty("git.build.time"));
        String c2r2 = String.format("git.build.version: %s",gitProperties.getProperty("git.build.version"));
        String c2r3 = String.format("git.commit.id:     %s",gitProperties.getProperty("git.commit.id"));
        String c2r4 = String.format("git.remote.origin: %s",gitProperties.getProperty("git.remote.origin.url"));
        String c3r1 = String.format("build-date: %s", getBuildProp("org.label-schema.build-date"));
        String c3r2 = String.format("vcs-ref: %s", getBuildProp("org.label-schema.vcs-ref"));
        String c3r3 = String.format("vcs-url: %s", getBuildProp("org.label-schema.vcs-url"));
        String c3r4 = String.format("description: %s", getBuildProp("org.label-schema.description"));

        if (getBuildProp("org.label-schema.build-date").equals("development")) {
            //skip showing the org.label.* values if they have not been generated by the build
            banner += String.format("    %-45s %s\n", c1r1, c2r1);
            banner += String.format("    %-45s %s\n", c1r2, c2r2);
            banner += String.format("    %-45s %s\n", c1r3, c2r3);
            banner += String.format("    %-45s %s\n", c1r4, c2r4);
        } else {
            banner += String.format("    %-45s %-60s %s\n", c1r1, c2r1, c3r1);
            banner += String.format("    %-45s %-60s %s\n", c1r2, c2r2, c3r2);
            banner += String.format("    %-45s %-60s %s\n", c1r3, c2r3, c3r3);
            banner += String.format("    %-45s %-60s %s\n", c1r4, c2r4, c3r4);
        }
        banner += "----------------------------------------------------------------------------------------------------";
        log.info(banner);
    }

    private Properties loadGitProperties() {
        Properties gitProperties = new Properties();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = contextClassLoader.getResourceAsStream("git.properties");
        if (resourceAsStream != null) {
            try {
                gitProperties.load(resourceAsStream);
                String gitId = gitProperties.getProperty("git.commit.id");
                log.info("git.commit.id={}", gitId);
            } catch (IOException e) {
                log.warn("Cannot load git.properties " + e.getMessage());
            }
        } else {
            log.info("Cannot display git status - git.properties not found in classpath");
        }
        return gitProperties;
    }

    private String deduceProtocol() {
        String retval = "http";
        String secure = getEnvProperty("security.require-ssl");
        if (secure != null && secure.equals("true")) {
            retval = "https";
        }
        return retval;
    }

    private String deduceAppNameVersion() {
        String name = getEnvProperty("spring.application.name");
        if (name == null) {
            name = "Application";
        }
        String version = getEnvProperty("info.app.version");
        if (version != null) {
            name += ":" + version;
        }
        return name;
    }

    private String getBuildProp(String key) {
        String retval = "unknown";
        if (buildProperties.get(key) != null) {
            retval = buildProperties.get(key);
        }
        return retval;
    }

    private String getEnvProperty(String key) {
        try {
            return environment.getProperty(key);
        } catch (IllegalArgumentException ignored) { //i.e. it may need to be filtered first through build.gradle
            return "";
        }
    }
}
