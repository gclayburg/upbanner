package com.garyclayburg.upbanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.garyclayburg.upbanner.jarprobe.BootJarDumper;
import com.garyclayburg.upbanner.jarprobe.FileJarDumper;
import com.garyclayburg.upbanner.jarprobe.JarProbe;
import com.garyclayburg.upbanner.nestedjar.BootNestedJarDumper;
import com.garyclayburg.upbanner.oshiprobe.EmptyOshiProbe;
import com.garyclayburg.upbanner.oshiprobe.FullOshiProbe;
import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * <br><br>
 * Created 2021-04-15 11:55
 *
 * @author Gary Clayburg
 */
@EnableConfigurationProperties(UpbannerSettings.class)
public class DebugDumper {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(DebugDumper.class);
    public static final String BUILDINFOPROPERTIES_PREFIX = "build.";
    private ConfigurableEnvironment environment;
    private ConfigurableApplicationContext context;
    private WhatsUpProbes whatsUpProbes;
    private boolean checkedDebugOnce;

    private DebugDumper() {
        this.checkedDebugOnce = false;
    }

    private static class DebugDumperHolder {
        private static final DebugDumper INSTANCE = new DebugDumper();
    }

    public static DebugDumper getInstance(ConfigurableApplicationContext context) {
        DebugDumper instance = DebugDumperHolder.INSTANCE;
        instance.init(context);
        return instance;
    }

    public static DebugDumper getInstance() {
        DebugDumper instance = DebugDumperHolder.INSTANCE;
        if (instance.getWhatsUpProbes() == null) {
            log.warn("WhatsUpProbes not initialized properly with a context");
        }
        return instance;
    }

    private synchronized void init(ConfigurableApplicationContext context) {
        if (this.context == null) {
            this.context = context;
            this.environment = context.getEnvironment();
            OshiProbe oshiProbe = createOshiProbe();
            JarProbe jarProbe = createJarProbe();
            UpbannerSettings upbannerSettings = createUpbannerSettings();
            BuildProperties buildProperties = createStandInBuildProperties();
            this.whatsUpProbes = new WhatsUpProbes(environment, buildProperties, oshiProbe, jarProbe, upbannerSettings, context);
        }
    }

    public WhatsUpProbes getWhatsUpProbes() {
        return whatsUpProbes;
    }

    public void dumpAll() {
        if ((whatsUpProbes != null && whatsUpProbes.getProbeResult() != null)) {
            //e.g. after a devtools restart and we already did a upbanner.debug probe
            //     once, which was saved
            whatsUpProbes.dumpAll();
        } else if (!this.checkedDebugOnce
                   && environment != null) {
            this.checkedDebugOnce = true;
            if (environment.getProperty("upbanner.debug") != null
                && environment.getProperty("upbanner.debug").equalsIgnoreCase("true")) {
                //e.g. not running under devtools or we are checking for the need to probe for
                // the first time
                // environment may be available, but environment.getProperty() can
                // fail with a NPE when using undertow and spring boot devtools
                // So this workaround is to avoid checking for upbanner.debug more than once
                // Instead, we just cache the first result and use that for any subsequent
                // app restarts like devtools might do
                whatsUpProbes.dumpAll();
            }
        }
    }

    private UpbannerSettings createUpbannerSettings() {
        UpbannerSettings upbannerSettings = new UpbannerSettings();
        upbannerSettings.setDebug(true);
        // environment.getProperty(String name) uses spring relaxed binding, i.e.
        // environment.getProperty("upbanner.show-banner").equals(environment.getProperty("upbanner.showBanner")
        if (environment.getProperty("upbanner.show-banner") != null &&
            environment.getProperty("upbanner.show-banner").equalsIgnoreCase("false")) {
            // default showbanner is true, so we need to adjust
            upbannerSettings.setShowBanner(false);
            log.info("we WILL show banner "+ environment.getProperty("upbanner.show-banner"));
        }
        return upbannerSettings;
    }

    /**
     * This creates a JarProbe class that can be used before the jarProbe bean is created
     * by Spring.  We need this so that we can show debug info early on in the boot process.
     * @return JarProbe
     */
    private JarProbe createJarProbe() {
        if (conditionalOnClass("org.springframework.boot.loader.jar.JarFile")) {
            if (BootJarDumper.weAreRunningUnderSpringBootExecutableJar()) {
                return new BootJarDumper();
            } else {
                return new FileJarDumper();
            }
        } else {
            if (conditionalOnClass("org.springframework.boot.loader.jar.NestedJarFile")){
                log.debug("Running under spring boot 3");
                if (BootNestedJarDumper.weAreRunningUnderSpringBootExecutableJar()) {
                    log.debug("Running under executable jar");
                    return new BootNestedJarDumper();
                } else {
                    return new FileJarDumper();
                }
            }
            //avoid probing if we know for sure we can't be running in a JarFile or expanded JarFile
            return new FileJarDumper();
        }
    }

    private OshiProbe createOshiProbe() {
        if (conditionalOnClass("oshi.SystemInfo")) {
            return new FullOshiProbe();
        } else {
            return new EmptyOshiProbe();
        }
    }

    private boolean conditionalOnClass(String className) {
        boolean classExists = false;
        try {
            this.getClass().getClassLoader().loadClass(className);
            classExists = true;
        } catch (ClassNotFoundException ignored) {
        }
        return classExists;
    }

    /**
     * This basically mirrors what {@link ProjectInfoAutoConfiguration#buildProperties()}
     * does.  We need to do it here because Beans are not ready yet by the time we need to
     * create the report about what is (will be) in it.
     * @return BuildProperties
     */
    private BuildProperties createStandInBuildProperties() {
        Properties targetProperties = new Properties();
        try (InputStream buildInfoPropertiesStream = this.getClass().getClassLoader().getResourceAsStream("/META-INF/build-info.properties")) {
            if (buildInfoPropertiesStream != null) {
                Properties sourceProperties = new Properties();
                sourceProperties.load(buildInfoPropertiesStream);
                for (String key : sourceProperties.stringPropertyNames()) {
                    if (key.startsWith(BUILDINFOPROPERTIES_PREFIX)) {
                        targetProperties.put(key.substring(BUILDINFOPROPERTIES_PREFIX.length()), sourceProperties.get(key));
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return new BuildProperties(targetProperties);
    }

    //todo show properties that are overridden and where/how they were overridden
    public void printProperties() {
        for (EnumerablePropertySource propertySource : findPropertiesPropertySources()) {
            log.info("******* " + propertySource.getName() + " *******");
            String[] propertyNames = propertySource.getPropertyNames();
            Arrays.sort(propertyNames);
            for (String propertyName : propertyNames) {
                String resolvedProperty = environment.getProperty(propertyName);
                String sourceProperty = propertySource.getProperty(propertyName).toString();
                if(resolvedProperty.equals(sourceProperty)) {
                    log.info("{}={}", propertyName, resolvedProperty);
                }else {
                    log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
                }
            }
        }
    }

    private List<EnumerablePropertySource> findPropertiesPropertySources() {
        List<EnumerablePropertySource> propertiesPropertySources = new LinkedList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource) propertySource);
            }
        }
        return propertiesPropertySources;
    }
}
