package com.garyclayburg.upbanner.jarprobe;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

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

    @Override
    public void createSnapshotJarReport(StringBuilder probeOut) {
        if (FileJarDumper.class.getClassLoader() instanceof URLClassLoader) {
            // this will work with both expanded boot jar,
            // and running from IntelliJ IDEA (normal classpath file entries)
            // and when running with boot jar (java -jar whateverapp.jar)
            probeOut.append("\n  Manifest dump for SNAPSHOT jar dependencies via classloader...").append(System.lineSeparator());
            Arrays.stream(((URLClassLoader) FileJarDumper.class.getClassLoader()).getURLs()).forEach(url -> {
                if (url.getProtocol().equals("file")) {
                    processEntry(probeOut,url.getPath());
                } else {
                    probeOut.append("WARN - Cannot decipher url ").append(url).append(System.lineSeparator());
                }
            });
        } else {
            // we need this so that it will run when tests are executed from maven
            // apparently maven uses a different classloader
            // jib created docker images also use use this
            // ./gradlew bootRun also uses this
            createSnapshotJarReportFromJavaClassPath(probeOut);
        }
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
