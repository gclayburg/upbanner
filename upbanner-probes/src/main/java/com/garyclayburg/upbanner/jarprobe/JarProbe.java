package com.garyclayburg.upbanner.jarprobe;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2020-12-07 11:56
 *
 * @author Gary Clayburg
 */
public abstract class JarProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(JarProbe.class);

    public abstract void createSnapshotJarReport(StringBuilder stringBuilder);

    public abstract Manifest getManifest();

    public void showManifest(StringBuilder probeOut, Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        formatAttributes(probeOut, mainAttributes);
    }

    protected void formatAttributes(StringBuilder probeOut, Attributes mainAttributes) {
        mainAttributes.forEach((name, value) -> probeOut.append("  ").append(name).append(":").append(value).append("\n"));
    }

    protected boolean shouldShowManifest(String entry) {
        /*
        ex of jar loaded from gradle cache:
        classloader.URL: file:/home/gclaybur/.gradle/caches/modules-2/files-2.1/com.garyclayburg/upbanner-starter/2.1.2-SNAPSHOT/9b356f3d0228e37db85d38f64a585f2f9fb5b6c5/upbanner-starter-2.1.2-SNAPSHOT.jar

        ex of jar loaded from local maven repo
        classpath: /home/gclaybur/.m2/repository/com/garyclayburg/upbanner-starter/2.1.2-SNAPSHOT/upbanner-starter-2.1.2-20210326.185920-25.jar

        ex of jar loaded from expanded war inside docker
        classloader.URL: jar:file:/home/springboot/app/WEB-INF/lib/stepsapi-0.8.1-SNAPSHOT.jar!/
         */
        return entry != null && (entry.matches(".*-SNAPSHOT/.*") || entry.matches(".*-SNAPSHOT.jar.*"));
    }

    protected void showJarName(StringBuilder probeOut, String name) {
        probeOut.append(name).append("\n");
    }

    public abstract void init(StringBuilder probe);

    public abstract void createRootManifestReport(StringBuilder probe);

    public abstract Manifest getManifest(String name) throws IOException;

    boolean isJar(URL url) {
        String path = url.getPath();
        return isJar(path);
    }

    public boolean isJar(String path) {
        return path.matches(".*\\.jar!*[/\\\\]*");
    }


}
