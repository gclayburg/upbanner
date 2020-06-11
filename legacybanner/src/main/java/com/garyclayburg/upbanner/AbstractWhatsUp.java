package com.garyclayburg.upbanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

/**
 * <br><br>
 * Created 2020-06-09 10:41
 *
 * @author Gary Clayburg
 */
public abstract class AbstractWhatsUp {
    protected Environment environment;
    protected BuildProperties buildProperties;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AbstractWhatsUp.class);

    public AbstractWhatsUp(Environment environment, BuildProperties buildProperties) {
        this.environment = environment;
        this.buildProperties = buildProperties;
    }

    abstract public void printVersion(int localPort);

    protected Properties loadGitProperties() {
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

    protected String deduceProtocol() {
        String retval = "http";
        String secure = getEnvProperty("security.require-ssl");
        if (secure != null && secure.equals("true")) {
            retval = "https";
        }
        return retval;
    }

    protected String deduceAppNameVersion() {
        String name = getEnvProperty("spring.application.name");
        if (name == null) {
            name = "Application";
        }
        String version = getEnvProperty("info.app.version");
        if (version != null) {
            name += ":" + version;
        } else {
            version = getEnvProperty("git.build.version");
            if (version != null) {
                name += ":" + version;
            }
        }
        return name;
    }

    protected String getBuildProp(String key) {
        String retval = "unknown";
        if (buildProperties.get(key) != null) {
            retval = buildProperties.get(key);
        }
        return retval;
    }

    protected String getEnvProperty(String key) {
        try {
            return environment.getProperty(key);
        } catch (IllegalArgumentException ignored) { //i.e. it may need to be filtered first through build.gradle
            return "";
        }
    }

    protected void dumpSystemProperties() {
        log.info("system properties dump");
        Properties systemProperties = System.getProperties();
        TreeMap tm = new TreeMap(systemProperties);
        for (Object o : tm.keySet()) {
            String key = (String) o;
            log.info(key + ": " + tm.get(o));
        }
        Map<String, String> getenv = new TreeMap<>(System.getenv());
        log.info("system environment dump");
        for (String key : getenv.keySet()) {
            log.info("env " + key + ": " + getenv.get(key));
        }
    }


}
