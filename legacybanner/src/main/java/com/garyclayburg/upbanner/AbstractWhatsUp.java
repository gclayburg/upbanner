package com.garyclayburg.upbanner;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
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

    public static final String MEM_TOTAL_REGEX = "^MemTotal:\\s*(\\d+)\\s*kB.*$";
    public static final Pattern MEM_TOTAL_REGEX_PATTERN = Pattern.compile(MEM_TOTAL_REGEX);
    private final OshiProbe oshiProbe;
    protected Environment environment;
    protected BuildProperties buildProperties;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AbstractWhatsUp.class);

    public AbstractWhatsUp(Environment environment, BuildProperties buildProperties, OshiProbe oshiProbe) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.oshiProbe = oshiProbe;
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

    protected void dumpAll() {
        StringBuilder probeOut = new StringBuilder();
        oshiProbe.createReport(probeOut);
        dumpSystemProperties(probeOut);
        dumpENV(probeOut);
        dumpBuildProperties(probeOut);
        dumpGitProperties(probeOut);
        dumpJVMargs(probeOut);
        dumpMemory(probeOut);
        if (log.isInfoEnabled()) {
            log.info("OSHI probe" + System.lineSeparator() + probeOut.toString());
        } else {
            log.warn("INFO logging is disabled. showing requested debug info as WARN instead");
            log.warn("OSHI probe" + System.lineSeparator() + probeOut.toString());
        }
    }

    protected void dumpMemory(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  JVM memory").append(System.lineSeparator());
        probeOut.append(String.format("JVM heap free memory:  %15s (%sm)",
                Runtime.getRuntime().freeMemory(),
                Runtime.getRuntime().freeMemory() / 1024 / 1024))
                .append(System.lineSeparator());
        probeOut.append(String.format("JVM heap total memory: %15s (%sm)",
                Runtime.getRuntime().totalMemory(),
                Runtime.getRuntime().totalMemory() / 1024 / 1024))
                .append(System.lineSeparator());
        probeOut.append(String.format("JVM heap max memory:   %15s (%sm)",
                Runtime.getRuntime().maxMemory(),
                Runtime.getRuntime().maxMemory() / 1024 / 1024))
                .append(System.lineSeparator());
        probeOut.append(String.format("JVM heap max memory:   %15s (%sm)",
                Runtime.getRuntime().maxMemory(),
                Runtime.getRuntime().maxMemory() / 1024 / 1024))
                .append(System.lineSeparator());
        long jvmMaxMemoryKb = Runtime.getRuntime().maxMemory() / 1024;
        File file = Paths.get("/sys/fs/cgroup/memory/memory.limit_in_bytes").toFile();
        String memoryLimitInBytes = "unknown";
        String memoryLimitMB = "unknown";
        long cgroupsMemLimitKb = Long.MAX_VALUE;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            if (file.canRead()) {

                memoryLimitInBytes = br.readLine();
                long memLimitMB = Long.parseLong(memoryLimitInBytes) / 1024 / 1024;
                cgroupsMemLimitKb = Long.parseLong(memoryLimitInBytes) / 1024;
                memoryLimitMB = String.valueOf(memLimitMB);
            }
        } catch (IOException ignored) {
        }
        File procMeminfo = new File("/proc/meminfo");
        long memTotalKbLong = Long.MAX_VALUE;
        try (BufferedReader br = new BufferedReader(new FileReader(procMeminfo))) {
            if (procMeminfo.canRead()) {
                String memTotalKb = br.lines()
                        .filter(line -> line.matches(MEM_TOTAL_REGEX))
                        .map(line -> {
                            Matcher m = MEM_TOTAL_REGEX_PATTERN.matcher(line);
                            String memKb = "";
                            if (m.find()) {
                                memKb = m.group(1);
                            }
                            return memKb;
                        })
                        .reduce("", (a, b) -> b);
                memTotalKbLong = Long.parseLong(memTotalKb);
                probeOut.append(String.format("OS Memory installed: %17s (%sm)",
                        memTotalKbLong * 1024,
                        memTotalKbLong / 1024)).append(System.lineSeparator());
            }
        } catch (IOException ignored) {
        }
        if (cgroupsMemLimitKb >= memTotalKbLong) { //no mem limit imposed on our cgroup
            probeOut.append("cgroups memory limit:        unlimited").append(System.lineSeparator());
        } else { // our container was started with limited memory
            if (cgroupsMemLimitKb < jvmMaxMemoryKb) {
                log.warn(String.format("cgroups memory limit:  %15s (%sm)", memoryLimitInBytes, memoryLimitMB));
                log.warn("JVM max heap size is too big for our cgroup." + System.lineSeparator() +
                         "  This JVM will likely become unstable under memory pressure, i.e.," + System.lineSeparator() +
                         "  the kernel could rudely kill the JVM if it requests too much memory. ");
            } else {
                probeOut.append(String.format("cgroups memory limit:  %15s (%sm)",
                        memoryLimitInBytes, memoryLimitMB)).append(System.lineSeparator());
            }
        }
    }

    /**
     * convert stored time to Instant<br>
     * We don't rely on {@link BuildProperties} class to convert this because
     * BuildProperties has a different API dealing with Date and Time
     * between Spring Boot version 1.x and 2.x.  We want upbanner
     * to work with either.
     *
     * @param key name of key that holds a time value in milliseconds
     * @return Instant
     */
    public Instant getInstant(String key) {
        String time = buildProperties.get(key);
        if (time != null) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(time));
            } catch (NumberFormatException ignored) {
                // Not valid epoch time
            } catch (DateTimeException ignored) {
            }
        }
        return null;
    }

    public void dumpBuildProperties(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  build-info.properties dump").append(System.lineSeparator());
        formatBuildTime(probeOut);
        buildProperties.forEach(prop -> probeOut.append("buildprop ").append(prop.getKey()).append(": ").append(prop.getValue()).append(System.lineSeparator()));
    }

    private void formatBuildTime(StringBuilder probeOut) {
        //"time" will be stored differently when running under spring boot 1.x, compared to spring boot 2.x
        // The Spring Boot 1.x BuildProperties has a bug where it does not store time as millis,
        // even though it tries.  The result is that time is stored as a human readable form.
        // Spring Boot 2.x does parse the time correctly into millis, so this value must be formatted
        // to be human readable.
        Instant time = getInstant("time");
        if (time != null) {
            try {
                Date date = Date.from(time);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

                probeOut.append("build time: ").append(formatter.format(date)).append(System.lineSeparator());
            } catch (IllegalArgumentException ignored) {
                // this is not the build time you were looking for anyway.
            }
        }
    }

    protected void dumpGitProperties(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  git.properties dump").append(System.lineSeparator());
        loadGitProperties().forEach((k, v) -> probeOut.append("gitprop ").append(k).append(": ").append(v).append(System.lineSeparator()));
    }

    protected void dumpSystemProperties(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  system properties dump").append(System.lineSeparator());
        System.getProperties().forEach((k, v) -> probeOut.append("prop ").append(k).append(": ").append(v).append(System.lineSeparator()));
    }

    protected void dumpENV(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  system environment dump").append(System.lineSeparator());
        System.getenv().forEach((key, val) -> probeOut.append("env ").append(key).append(": ").append(val).append(System.lineSeparator()));
    }

    protected void dumpJVMargs(StringBuilder probeOut) {
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  JVM args").append(System.lineSeparator());
        ManagementFactory.getRuntimeMXBean().getInputArguments().forEach(arg ->
                probeOut.append("JVM arg: ").append(arg).append(System.lineSeparator()));
    }
}
