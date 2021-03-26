package com.garyclayburg.upbanner.jarprobe;

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
        return entry != null && entry.matches(".*SNAPSHOT.jar");
    }

    protected void showJarName(StringBuilder probeOut, String name) {
        probeOut.append(name).append("\n");
    }

    public abstract void init(StringBuilder probe);

    public abstract void createRootManifestReport(StringBuilder probe);
}
