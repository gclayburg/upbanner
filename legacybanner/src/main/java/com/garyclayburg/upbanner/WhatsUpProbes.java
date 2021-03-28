package com.garyclayburg.upbanner;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garyclayburg.upbanner.jarprobe.JarProbe;
import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2020-06-09 10:41
 *
 * @author Gary Clayburg
 */
@Component
public class WhatsUpProbes {

    public static final String MEM_TOTAL_REGEX = "^MemTotal:\\s*(\\d+)\\s*kB.*$";
    public static final Pattern MEM_TOTAL_REGEX_PATTERN = Pattern.compile(MEM_TOTAL_REGEX);
    public static final int DEFAULT_VALUE = -42;
    public static final String DEFAULT_VALUE_STRING = "";
    public static final String CGROUP_FILE = "/proc/self/cgroup";
    private final OshiProbe oshiProbe;
    private final JarProbe jarProbe;
    private final UpbannerSettings upbannerSettings;
    protected Environment environment;
    protected BuildProperties buildProperties;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUpProbes.class);
    private String applicationName;
    protected int listeningPort;

    public WhatsUpProbes(Environment environment, BuildProperties buildProperties, OshiProbe oshiProbe, JarProbe jarProbe, UpbannerSettings upbannerSettings) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.oshiProbe = oshiProbe;
        this.jarProbe = jarProbe;
        this.upbannerSettings = upbannerSettings;
    }

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
            log.debug("Cannot display git status - git.properties not found in classpath");
        }
        return gitProperties;
    }

    public String deduceProtocol() {
        String retval = "http";
        String secure = getEnvProperty("security.require-ssl");
        if (secure != null && secure.equals("true")) {
            retval = "https";
        }
        return retval;
    }

    public String deduceAppNameVersion() {
        String name = getAppName();

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

    public String getAppName() {
        String name = getEnvProperty("spring.application.name");
        if (name == null) {
            name = applicationName;
            if (applicationName == null) {
                name = getMain("Application");
            }
        }
        return name;
    }

    public String getMain(String name) {
        String javaCommand = System.getProperty("sun.java.command");
        if (javaCommand != null) {
            name = getMainStart(javaCommand);
        }
        return name;
    }

    public String getMainStart(String javaCommand) {
        String name = "Application";
        String mainName = convertSunJavaCommand(javaCommand);
        if (javaCommand.contains("JarLauncher") || javaCommand.contains("WarLauncher")) {
            // we are running a spring boot jar or expanded jar.
            // Start-Class attribute from the expanded Manifest file
            // has the real class that will be executed first
            name = extractStartClassName(name);
        } else if (mainName != null && mainName.equals("jar")) {
            //we could be running a spring boot bundled jar OR something more
            // opaque like surefire test runner
            name = extractStartClassName(name);
        }
        return name;
    }

    private String extractStartClassName(String mainName) {
        Manifest manifest = jarProbe.getManifest();
        if (manifest != null) {
            Attributes mainAttributes = manifest.getMainAttributes();
            if (log.isDebugEnabled()) {
                StringBuilder stringBuilder = new StringBuilder();
                jarProbe.showManifest(stringBuilder, manifest);
                log.debug("root manifest found is: \n" + stringBuilder.toString());
            }
            String startClassName = mainAttributes.getValue("Start-Class");
            log.debug(" start class is " + startClassName);
            if (startClassName != null) {
                mainName = convertStartClass(startClassName);
            }
        }
        return mainName;
    }

    String convertSunJavaCommand(String javaCommand) {
        // javaCommand when running specified java main in IntelliJ:
        // com.garyclayburg.BootUp

        // javaCommand when running testClass via IntelliJ:
        // com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 com.example.demo.DemoApplicationTests

        // javaCommand when running testClass via maven:
        // /home/gclaybur/dev/gvsync/upbanner/webjar244/target/surefire/surefirebooter2001082309298668663.jar /home/gclaybur/dev/gvsync/upbanner/webjar244/target/surefire 2021-03-22T08-45-40_408-jvmRun1 surefire5666214610931227172tmp surefire_04846830553387518722tmp

        // javaCommand when running testClass via gradle:
        // worker.org.gradle.process.internal.worker.GradleWorkerMain 'Gradle Test Executor 2'

        // javaCommand when running as standalone jar like this:  java -jar ./webjar244/target/demo-2.1.2-SNAPSHOT.jar
        // ./webjar244/target/demo-2.1.2-SNAPSHOT.jar
        log.debug("javaCommand is: " + javaCommand);
        String stripped = javaCommand.replaceFirst(" .*$", "");
        return convertStartClass(stripped);
    }

    String convertStartClass(String startClassName) {
        if (startClassName != null) {
            String[] split = startClassName.split("\\.");
            return split[split.length - 1];
        }
        return null;
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

    public void dumpAll() {
        StringBuilder probe = new StringBuilder();
        section(probe, "\n===== What OS and hardware are we running on =====");
        oshiProbe.createReport(probe);
        section(probe, "\n===== What OS resources are limited ==============");
        dumpCPUlimits(probe);
        dumpMemoryLimits(probe);
        section(probe, "\n===== What environment are we running with =======");
        dumpSystemProperties(probe);
        dumpENV(probe);
        section(probe, "\n===== How was it built ===========================");
        dumpBuildProperties(probe);
        section(probe, "\n===== How was it started =========================");
        jarProbe.init(probe);
        dumpStartupCommandJVMargs(probe);
        section(probe, "\n===== What is running ============================");
        dumpGitProperties(probe);
        jarProbe.createRootManifestReport(probe);
        jarProbe.createSnapshotJarReport(probe);
        if (log.isInfoEnabled()) {
            log.info("Environment probe:" + System.lineSeparator() + probe.toString());
        } else { // the operator wants to show the information.  Lets not also force them to enable INFO
            log.warn("INFO logging is disabled. showing requested debug info as WARN instead");
            log.warn("Environment probe:" + System.lineSeparator() + probe.toString());
        }
    }

    protected void section(StringBuilder probeOut, String header) {
        probeOut.append(header).append(System.lineSeparator());
    }

    public void dumpCPUlimits(StringBuilder probeOut) {
        probeOut.append("  CPU limits").append(System.lineSeparator());
        probeOut.append("available logical processors: ").append(Runtime.getRuntime().availableProcessors())
                .append(System.lineSeparator());
        CgroupCpuStats cgroupCpuStats = new CgroupCpuStats();
        cgroupCpuStats.setCfs_period_us(readLong(DEFAULT_VALUE, "/sys/fs/cgroup/cpu/cpu.cfs_period_us"));
        if (cgroupCpuStats.getCfs_period_us() != DEFAULT_VALUE) {
            probeOut.append("cfs_period_us: ").append(cgroupCpuStats.getCfs_period_us())
                    .append(System.lineSeparator());
        }
        cgroupCpuStats.setCfs_quota_us(readLong(DEFAULT_VALUE, "/sys/fs/cgroup/cpu/cpu.cfs_quota_us"));
        if (cgroupCpuStats.getCfs_quota_us() != DEFAULT_VALUE) {
            probeOut.append("cpu.cfs_quota_us: ").append(cgroupCpuStats.getCfs_quota_us())
                    .append(System.lineSeparator());
        }
        cgroupCpuStats.setCpu_shares(readLong(DEFAULT_VALUE, "/sys/fs/cgroup/cpu,cpuacct/cpu.cpu_shares"));
        if (cgroupCpuStats.getCpu_shares() != DEFAULT_VALUE) {
            probeOut.append("cpu.shares: ").append(cgroupCpuStats.getCpu_shares())
                    .append(System.lineSeparator());
        }
        cgroupCpuStats.setCpuacct_usage(readLong(DEFAULT_VALUE, "/sys/fs/cgroup/cpu,cpuacct/cpuacct.usage"));
        if (cgroupCpuStats.getCpuacct_usage() != DEFAULT_VALUE) {
            probeOut.append("cpuacct.usage: ").append(cgroupCpuStats.getCpuacct_usage() / 1000.0 / 1000.0 / 1000.0)
                    .append(System.lineSeparator());
        }
        cgroupCpuStats.setCpuacct_usage_percpu(readString(DEFAULT_VALUE_STRING, "/sys/fs/cgroup/cpu,cpuacct/cpuacct.usage_percpu"));
        if (!cgroupCpuStats.getCpuacct_usage_percpu().equals(DEFAULT_VALUE_STRING)) {
            probeOut.append("cpuacct.usage_percpu: ").append(cgroupCpuStats.getCpuacct_usage_percpu())
                    .append(System.lineSeparator());
        }
        cgroupCpuStats.setCpuset(readString(DEFAULT_VALUE_STRING, "/sys/fs/cgroup/cpuset/cpuset.cpus"));
        if (!cgroupCpuStats.getCpuset().equals(DEFAULT_VALUE_STRING)) {
            probeOut.append("cpuset: ").append(cgroupCpuStats.getCpuset())
                    .append(System.lineSeparator());
        }

        File cpustat = new File("/sys/fs/cgroup/cpu/cpu.stat");
        if (cpustat.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(cpustat))) {
                String line = br.readLine();
                while (line != null) {
                    Pattern NR_PERIODS_P = Pattern.compile("^nr_periods\\s+(\\d+)$");
                    Matcher m = NR_PERIODS_P.matcher(line);
                    if (m.find()) {
                        cgroupCpuStats.setNr_periods(Long.parseLong(m.group(1)));
                        probeOut.append("nr_periods: ").append(cgroupCpuStats.getNr_periods())
                                .append(System.lineSeparator());
                    }

                    Pattern NR_THROTTLDED_P = Pattern.compile("^nr_throttled\\s+(\\d+)$");
                    Matcher mt = NR_THROTTLDED_P.matcher(line);
                    if (mt.find()) {
                        cgroupCpuStats.setNr_throttled(Long.parseLong(mt.group(1)));
                        probeOut.append("nr_throttled: ").append(cgroupCpuStats.getNr_throttled())
                                .append(System.lineSeparator());
                    }

                    Pattern THROTTLED_TIME_P = Pattern.compile("^throttled_time\\s+(\\d+)$");
                    Matcher mtt = THROTTLED_TIME_P.matcher(line);
                    if (mtt.find()) {
                        cgroupCpuStats.setThrottled_time(Long.parseLong(mtt.group(1)));
                        probeOut.append("throttled_time: ").append(cgroupCpuStats.getThrottled_time())
                                .append(System.lineSeparator());
                    }

                    line = br.readLine();
                }
            } catch (IOException ignored) {

            }
        }
    }

    private String readString(String defaultValue, String pathname) {
        File file = new File(pathname);
        if (file.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                defaultValue = br.readLine();
            } catch (IOException ignored) {
            }
        }
        return defaultValue;
    }

    private long readLong(long defaultValue, String pathname) {
        try {
            defaultValue = Long.parseLong(readString("", pathname));
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public void dumpMemoryLimits(StringBuilder probeOut) {
        /*
        Todo: add additional checks for cgroup cpu limitation
        https://fabiokung.com/2014/03/13/memory-inside-linux-containers/
        Are there environment variables CONTAINER_MAX_MEMORY and CONTAINER_MAX_CPU or MAX_CORE_LIMIT available?

Files relevant to container limits:
cores:
cpu_period_file="/sys/fs/cgroup/cpu/cpu.cfs_period_us"
cpu_quota_file="/sys/fs/cgroup/cpu/cpu.cfs_quota_us"
memory:
mem_file="/sys/fs/cgroup/memory/memory.limit_in_bytes"
         */
        probeOut.append("\n  JVM memory limits").append(System.lineSeparator());
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
        probeOut.append("  build-info.properties dump").append(System.lineSeparator());
        formatBuildTime(probeOut);
        if (buildProperties.getVersion() != null && !buildProperties.getVersion().equals("development build")) {
            buildProperties.forEach(prop -> probeOut.append("buildprop ").append(prop.getKey()).append(": ").append(prop.getValue()).append(System.lineSeparator()));
        }
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

    public void dumpGitProperties(StringBuilder probeOut) {
        probeOut.append("  git.properties dump").append(System.lineSeparator());
        loadGitProperties().forEach((k, v) -> probeOut.append("gitprop ").append(k).append(": ").append(v).append(System.lineSeparator()));
    }

    public void dumpSystemProperties(StringBuilder probeOut) {
        probeOut.append("  system properties dump").append(System.lineSeparator());
        System.getProperties().forEach((k, v) -> probeOut.append("prop ").append(k).append(": ").append(v).append(System.lineSeparator()));
    }

    public void dumpENV(StringBuilder probeOut) {
        probeOut.append("\n  system environment dump").append(System.lineSeparator());
        System.getenv().forEach((key, val) -> probeOut.append("env ").append(key).append(": ").append(val).append(System.lineSeparator()));
    }

    public void dumpStartupCommandJVMargs(StringBuilder probeOut) {
        probeOut.append("  JVM args/classloader URLs/startup command").append(System.lineSeparator());
        ManagementFactory.getRuntimeMXBean().getInputArguments().forEach(arg ->
                probeOut.append("JVM arg: ").append(arg).append(System.lineSeparator()));
        if (WhatsUpProbes.class.getClassLoader() instanceof URLClassLoader) {
            Arrays.stream(((URLClassLoader) WhatsUpProbes.class.getClassLoader()).getURLs()).forEach(url -> probeOut.append("classloader.URL: ").append(url).append(System.lineSeparator()));
            // e.g. when running with tomcat:
            // classloader.URL: file:/home/springboot/app/BOOT-INF/lib/log4j-api-2.13.3.jar
            // e.g. when running in docker but not a web app:
            // classloader.URL: jar:file:/home/springboot/app/BOOT-INF/lib/spring-core-4.3.24.RELEASE.jar!/
        } else {
            String classpath = System.getProperty("java.class.path");
            Arrays.stream(classpath.split(":")).forEach(cpEntry ->
                    probeOut.append("classpath: ").append(cpEntry).append(System.lineSeparator()));
        }

        String javaCmd = System.getProperty("sun.java.command");
        probeOut.append("Java command: ").append(javaCmd).append(System.lineSeparator());
        probeOut.append("Main: ").append(getMain("unknown")).append(System.lineSeparator());
        /*
        example Java command output
$ ./gradlew -Dupbanner.debug=true bootRun
Java command: com.garyclayburg.upbannerdemo.UpbannerdemoApplication
Main: UpbannerdemoApplication

$ java -jar build/libs/upbannerdemo-0.0.1-SNAPSHOT.jar --upbanner.debug=true
Java command: build/libs/upbannerdemo-0.0.1-SNAPSHOT.jar --upbanner.debug=true
Main: UpbannerdemoApplication

run upbannerdemo from IntelliJ
Java command: com.garyclayburg.upbannerdemo.UpbannerdemoApplication --server.port=8881
Main: UpbannerdemoApplication

docker image created from preparedocker gradle plugin
$ docker run registry:5000/upbannerdemo:latest --upbanner.debug=true
Java command: org.springframework.boot.loader.JarLauncher --upbanner.debug=true
Main: UpbannerdemoApplication

docker image created from jib/jhipster
$ docker run --rm -v /home/gclaybur/dev/groovyrundemo:/groovy -e GROOVYSCRIPT_HOME=/groovy -e JAVA_OPTS="-Dupbanner.debug=true" -p 10001:8080 rungroovy:latest
Java command: com.garyclayburg.rungroovy.RungroovyApp
Main: RungroovyApp

docker images created from spring boot:
$ ./gradlew clean build buildImage bootBuildImage && docker run docker.io/library/upbannerdemo:0.0.1-SNAPSHOT  --upbanner.debug=true
Java command: org.springframework.boot.loader.JarLauncher --upbanner.debug=true
Main: UpbannerdemoApplication

         */
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = convertStartClass(applicationName);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void printHostPortVersionGitBanner() {
        if (listeningPort == 0) {
            return;
        }
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
        String c1r2 = String.format("Local:      %s://localhost:%s", proto, listeningPort);
        String c1r3 = String.format("External:   %s://%s:%s ", proto, hostAddress, listeningPort);
        String c1r4 = String.format("Host:       %s://%s:%s ", proto, hostName, listeningPort);
        if (isDocker()) {
            c1r4 = String.format("Docker:     %s://%s:%s ", proto, hostName, listeningPort);
        } else if (isKubernetes()) {
            c1r4 = String.format("Kubernetes: %s://%s:%s ", proto, hostName, listeningPort);
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

    public boolean isDocker() {
        boolean isDocker = false;
        try {
            isDocker = Files.lines(Paths.get(CGROUP_FILE)).map(line ->
                    line.matches(".*/docker.*")).reduce(false, (first, found) -> found || first);
        } catch (IOException e) {
            // not linux?
        }
        return isDocker;
    }

    public boolean isKubernetes() {
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
