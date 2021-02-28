package com.garyclayburg.upbanner.jarprobe;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2021-01-17 10:47
 *
 * @author Gary Clayburg
 */
public class FileJarDumper extends JarProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FileJarDumper.class);
    public static final Pattern FILEURL = Pattern.compile("^file:(.*)!/$");
    private Manifest manifest;

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public void init(StringBuilder probe) {
//        probe.append("init fileJarDumper\n");
        if (isExpandedBootJar()) {
            ClassLoader fileJarDumperClassloader = FileJarDumper.class.getClassLoader();
            try {
//                probe.append("\n classloader is " + fileJarDumperClassloader).append(System.lineSeparator());
//                probe.append("\n root manifest is: " +
//                             fileJarDumperClassloader.getResource("META-INF/MANIFEST.MF"))
//                        .append(System.lineSeparator());
                manifest = new Manifest(fileJarDumperClassloader.getResourceAsStream("META-INF/MANIFEST.MF"));
                // this manifest is not that interesting since we are NOT running from  a boot jar
                // but it IS needed when we are running from an expanded boot jar.  That expanded jar
                // contains a MANIFEST.MF that has the Start-Class attribute we need to determine Main
            } catch (IOException e) {
                log.warn("can't load manifest " + fileJarDumperClassloader.getResource("META-INF/MANIFEST.MF"));
            }
        }
    }

    private boolean isExpandedBootJar() {
        boolean expandedBootJar = false;
        String javaCommand = System.getProperty("sun.java.command");
        if (javaCommand != null) {
            String name = convertSunJavaCommand(javaCommand);
            if (javaCommand.contains("JarLauncher") || javaCommand.contains("WarLauncher")|| (name != null && name.equals("jar"))) {
                expandedBootJar = true;
            }
        }
        return expandedBootJar;
    }

    String convertSunJavaCommand(String javaCommand) {
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


    @Override
    public void createSnapshotJarReport(StringBuilder probeOut) {
        if (FileJarDumper.class.getClassLoader() instanceof URLClassLoader) {
            // this will work with both expanded boot jar,
            // and running from IntelliJ IDEA (normal classpath file entries)
            // and when running with boot jar (java -jar whateverapp.jar)
            // and when running with "$ UPBANNER_DEBUG=true ./gradlew bootRun" from project using spring devtools
            probeOut.append("\n  Manifest dump for SNAPSHOT jar dependencies via classloader...").append(System.lineSeparator());


            Arrays.stream(((URLClassLoader) FileJarDumper.class.getClassLoader()).getURLs()).forEach(url -> processEntry(probeOut, inspectClasspathEntryURL(probeOut, url)));
        } else {
            // we need this so that it will run when tests are executed from maven
            // apparently maven uses a different classloader
            // jib created docker images also use use this
            // ./gradlew bootRun also uses this
            createSnapshotJarReportFromJavaClassPath(probeOut);
        }
    }

    String inspectClasspathEntryURL(StringBuilder probeOut, URL url) {
        if (url.getProtocol().equals("file")) {
            // e.g. when running with tomcat inside docker using expanded boot jar:
            // file:/home/springboot/app/BOOT-INF/lib/log4j-api-2.13.3.jar
            return url.getPath();
        } else {
            if (url.getProtocol().equals("jar")) {
                // e.g. when running outside of web container but inside docker using expanded boot jar:
                // jar:file:/home/springboot/app/BOOT-INF/lib/stepsapi-0.8.1-SNAPSHOT.jar!/
                Matcher matcher = FILEURL.matcher(url.getPath());
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
            probeOut.append("WARN - Cannot decipher url ").append(url).append(System.lineSeparator());
//            probeOut.append("proto: ").append(url.getProtocol()).append(System.lineSeparator());
//            probeOut.append("path: ").append(url.getPath()).append(System.lineSeparator());
//            probeOut.append("file: ").append(url.getFile()).append(System.lineSeparator());
        }
        return null;
    }

    public void createSnapshotJarReportFromJavaClassPath(StringBuilder probeOut) {
        probeOut.append("\n  Manifest dump for SNAPSHOT jar dependencies...").append(System.lineSeparator());
        String property = System.getProperty("java.class.path");
        Arrays.stream(property.split(":")).forEach(entry -> processEntry(probeOut, entry));
    }

    private void processEntry(StringBuilder probeOut, String entry) {
        if (shouldShowManifest(entry)) {
            try (FileInputStream fileInputStream = new FileInputStream(entry)) {
                showJarName(probeOut, entry);
                JarInputStream jarInputStream = new JarInputStream(fileInputStream);
                Manifest manifest = jarInputStream.getManifest();
                showManifest(probeOut, manifest);
            } catch (IOException e) {
                probeOut.append("WARN - Cannot read manifest from jar: ").append(entry).append("\n");
            }
        }
    }
}
