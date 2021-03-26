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
                    Arrays.stream(((URLClassLoader) BootJarDumper.class.getClassLoader()).getURLs()).forEach(url -> log.debug("URL "+url));
/* java-jar blah.jar:
codesource path: file:/home/gclaybur/dev/gvsync/upbanner/webjar1519/target/webjar1519-2.1.2-SNAPSHOT.jar!/BOOT-INF/lib/legacybanner-2.1.2-SNAPSHOT.jar!/
2021-03-26 08:48:48,392 [           main] DEBUG c.g.upbanner.jarprobe.BootJarDumper  - URL jar:file:/home/gclaybur/dev/gvsync/upbanner/webjar1519/target/webjar1519-2.1.2-SNAPSHOT.jar!/BOOT-INF/classes!/
2021-03-26 08:48:48,392 [           main] DEBUG c.g.upbanner.jarprobe.BootJarDumper  - URL jar:file:/home/gclaybur/dev/gvsync/upbanner/webjar1519/target/webjar1519-2.1.2-SNAPSHOT.jar!/BOOT-INF/lib/spring-boot-starter-web-1.5.19.RELEASE.jar!/
*/
/*
java -jar blah.war:
2021-03-26 08:52:56,040 [           main] DEBUG c.g.upbanner.jarprobe.BootJarDumper  - checking codesource path: file:/home/gclaybur/dev/gvsync/upbanner/weboshiwar244/target/weboshiwar244-2.1.2-SNAPSHOT.war!/WEB-INF/lib/legacybanner-2.1.2-SNAPSHOT.jar!/
2021-03-26 08:52:56,041 [           main] DEBUG c.g.upbanner.jarprobe.BootJarDumper  - URL jar:file:/home/gclaybur/dev/gvsync/upbanner/weboshiwar244/target/weboshiwar244-2.1.2-SNAPSHOT.war!/WEB-INF/classes!/
2021-03-26 08:52:56,041 [           main] DEBUG c.g.upbanner.jarprobe.BootJarDumper  - URL jar:file:/home/gclaybur/dev/gvsync/upbanner/weboshiwar244/target/weboshiwar244-2.1.2-SNAPSHOT.war!/WEB-INF/lib/jackson-core-2.11.4.jar!/
2
 */

                } else {
                    log.info("not URL classloader.");
                }
            }
            return createJarFile(probeOut, createTrimmedPath(probeOut,path));
        } catch (URISyntaxException | MalformedURLException e) {
            probeOut.append("WARN - CodeSource is not valid: ").append(codeSource != null ? codeSource.getLocation(): "null");
            return null;
        }
    }

    static String createTrimmedPath(StringBuilder probeOut, String path) {
        if (path == null) {
            log.debug("Cannot determine CodeSource location");
            probeOut.append("WARN - Cannot determine CodeSource location\n");
            return null;
        }
        if (path.lastIndexOf("!/BOOT-INF") > 0) { //we are running from boot jar
            return path.substring(0, path.lastIndexOf("!/BOOT-INF"));
        }
        if (path.lastIndexOf("!/WEB-INF") >0) { //we are running from boot war
            return path.substring(0, path.lastIndexOf("!/WEB-INF"));
        }
        log.debug("neither !/BOOT-INF nor !/WEB-INF found in path.  We are not running from a spring boot jar/war");
        probeOut.append("WARN - neither !/BOOT-INF nor !/WEB-INF found in path ").append(path)
                .append("  Is this not a spring boot executable jar?\n");
        return null;
    }

    private static JarFile createJarFile(StringBuilder probeOut, String path) throws IOException {
        if (path == null) {
            return null;
        }
        path = path.replace("file:", "");

        File root = new File(path);
        if (root.isDirectory()) {
            log.debug("WARN - root is a directory.  Is this a valid spring boot jar?\n");
            probeOut.append("WARN - root is a directory.  Is this a valid spring boot jar?\n");
            return null;
        }
        if (!root.exists()) {
            log.debug("WARN - root does not exist.  Is this a valid spring boot jar?\n");
            probeOut.append("WARN - root does not exist.  Is this a valid spring boot jar?\n");
            return null;
        }
        return new JarFile(root);
    }
}
