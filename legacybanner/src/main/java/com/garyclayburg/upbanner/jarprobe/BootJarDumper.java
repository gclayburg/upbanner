package com.garyclayburg.upbanner.jarprobe;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.loader.jar.JarFile;

/**
 * <br><br>
 * Created 2021-01-17 11:49
 *
 * @author Gary Clayburg
 */
public class BootJarDumper extends JarProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(BootJarDumper.class);
    private Manifest manifest;
    private JarFile jarFile;

    public static boolean weAreRunningUnderSpringBootExecutableJar() {
        boolean weAreRunningUnderSpringBootExecutableJar = false;
        try {
            StringBuilder probeOut = new StringBuilder();
            if (getBootJarFile(probeOut) != null) {
//                log.info("we are spring boot executable jar:\n" + probeOut.toString());
                weAreRunningUnderSpringBootExecutableJar = true;
//            } else {
//                log.info("apparently not spring boot:\n" + probeOut.toString());
            }
        } catch (IOException ignored) {
        }
        return weAreRunningUnderSpringBootExecutableJar;
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public void init(StringBuilder probeOut) {
        probeOut.append("  init BootJarDumper\n");
        try {
            jarFile = getBootJarFile(probeOut);
            if (jarFile != null) {
                manifest = jarFile.getManifest();
            } else {
                probeOut.append("    WARN - Cannot probe boot jar")
                        .append("\n");
            }
        } catch (IOException e) {
            probeOut.append("  WARN - Cannot open boot jar\n")
                    .append("\n");
        }
    }

    @Override
    public void createSnapshotJarReport(StringBuilder probeOut) {
        probeOut.append("\n  Manifest dump for SNAPSHOT boot jar dependencies...").append(System.lineSeparator());
        if (manifest == null) {
            init(probeOut);
        }
        showJarName(probeOut, jarFile.getName());
        showManifest(probeOut, manifest);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            try {
                if (jarEntry.getName().endsWith(".jar")) {
                    if (shouldShowManifest(jarEntry.getName())) {
                        showJarName(probeOut, jarEntry.getName());
                        JarFile nestedJarFile = jarFile.getNestedJarFile(jarEntry);
                        Manifest nestedManifest = nestedJarFile.getManifest();
                        showManifest(probeOut, nestedManifest);
                    }
                }
            } catch (IOException e) {
                probeOut.append("  WARN - Cannot open ").append(jarEntry.getName())
                        .append("\n");
            }
        }
    }

    public static JarFile getBootJarFile(StringBuilder probeOut) throws IOException {
        ProtectionDomain protectionDomain = BootJarDumper.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        try {
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String path = location == null ? null : location.toURL().getPath();
            if (log.isDebugEnabled()) {
                log.debug("checking codesource path: " + path);
                if (BootJarDumper.class.getClassLoader() instanceof URLClassLoader) {
                    Arrays.stream(((URLClassLoader) BootJarDumper.class.getClassLoader()).getURLs()).forEach(url -> log.info("URL "+url));
                } else {
                    log.info("not URL classloader.");
                }
            }
            if (path == null) {
                probeOut.append("WARN - Cannot determine CodeSource location\n");
                return null;
            }
            if (path.lastIndexOf("!/BOOT-INF") <= 0) {
                probeOut.append("WARN - !/BOOT-INF not found in path ").append(path)
                        .append("  Is this not a spring boot executable jar?\n");
                return null;
            }
            path = path.substring(0, path.lastIndexOf("!/BOOT-INF"));
            path = path.replace("file:", "");

            File root = new File(path);
            if (root.isDirectory()) {
                probeOut.append("WARN - root is not a directory.  Is this a valid spring boot jar?\n");
                return null;
            }
            if (!root.exists()) {
                probeOut.append("WARN - root does not exist.  Is this a valid spring boot jar?\n");
                return null;
            }
            return new JarFile(root);
        } catch (URISyntaxException | MalformedURLException e) {
            probeOut.append("WARN - CodeSource is not valid: ").append(codeSource.getLocation());
            return null;
        }
    }
}
