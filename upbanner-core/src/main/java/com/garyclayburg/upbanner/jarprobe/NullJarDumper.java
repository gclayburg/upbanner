package com.garyclayburg.upbanner.jarprobe;

import java.io.IOException;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2021-04-05 12:01
 *
 * @author Gary Clayburg
 */
public class NullJarDumper extends JarProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(NullJarDumper.class);

    @Override
    public void createSnapshotJarReport(StringBuilder stringBuilder) {
    }

    @Override
    public Manifest getManifest() {
        return null;
    }

    @Override
    public void init(StringBuilder probe) {

    }

    @Override
    public void createRootManifestReport(StringBuilder probe) {
    }

    @Override
    public Manifest getManifest(String name) throws IOException {
        log.warn("cannot get Manifest for " + name + "   is upbanner.debug enabled?");
        return null;
    }
}
