package com.garyclayburg.upbanner;

import com.garyclayburg.upbanner.jarprobe.BootJarDumper;
import com.garyclayburg.upbanner.jarprobe.FileJarDumper;
import com.garyclayburg.upbanner.jarprobe.JarProbe;
import com.garyclayburg.upbanner.oshiprobe.EmptyOshiProbe;
import com.garyclayburg.upbanner.oshiprobe.FullOshiProbe;
import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br><br>
 * Created 2020-12-07 11:45
 *
 * @author Gary Clayburg
 */
@Configuration
public class ContainerVMEnvProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ContainerVMEnvProbe.class);

    /*
    We are using @ConditionalOnClass(name= "oshi.SystemInfo") instead of
                 @ConditionalOnClass({oshi.SystemInfo.class})
    because using the String version of the class name allows us to compile our class under
    Spring boot 1.x and have it be executed under Spring boot 1.x or 2.x
    If instead we were to use the non-String based version of this annotation,
    an application using this dependency along with Spring boot 2 ( at least it was noticed under Spring boot 2.4.2)
    would fail at runtime when the application is started with a non-helpful error message like this:

java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
	at sun.reflect.annotation.AnnotationParser.parseClassArray(AnnotationParser.java:724) ~[na:1.8.0_201]
	at sun.reflect.annotation.AnnotationParser.parseArray(AnnotationParser.java:531) ~[na:1.8.0_201]
	at sun.reflect.annotation.AnnotationParser.parseMemberValue(AnnotationParser.java:355) ~[na:1.8.0_201]
	at sun.reflect.annotation.AnnotationParser.parseAnnotation2(AnnotationParser.java:286) ~[na:1.8.0_201]
	at sun.reflect.annotation.AnnotationParser.parseAnnotations2(AnnotationParser.java:120) ~[na:1.8.0_201]
	at sun.reflect.annotation.AnnotationParser.parseAnnotations(AnnotationParser.java:72) ~[na:1.8.0_201]
	at java.lang.reflect.Executable.declaredAnnotations(Executable.java:599) ~[na:1.8.0_201]

    Using this mechanism to allow this dependency to execute under Spring boot 1.x or 2.x is somewhat questionable
    but it seems to work for now.
     */
    @ConditionalOnClass(name= "oshi.SystemInfo")
    @ConditionalOnMissingBean(name = "oshiProbe") // allow users of upbanner to specify their own oshiBean
    @Bean({"oshiProbe"})
    public OshiProbe oshiProbe() {
        return new FullOshiProbe();
    }

    @ConditionalOnMissingClass({"oshi.SystemInfo"})
    @ConditionalOnMissingBean(name = "oshiProbe")
    @Bean({"oshiProbe"})
    public OshiProbe oshiBeanEmpty() {
        log.info("com.github.oshi:oshi-core dependency not present.  Advanced probes disabled.");
        return new EmptyOshiProbe();
    }

    @ConditionalOnClass(name ="org.springframework.boot.loader.jar.JarFile")
    @Bean({"jarProbe"})
    public JarProbe jarDumper() {
        if (BootJarDumper.weAreRunningUnderSpringBootExecutableJar()) {
            return new BootJarDumper();
        } else {
            return new FileJarDumper();
        }
    }
    @Bean({"jarProbe"})
    @ConditionalOnMissingClass({"org.springframework.boot.loader.jar.JarFile"})
    public JarProbe jarDumperMissing() {
        log.info("JarFile is not in classpath!");
        return new FileJarDumper();
    }


}
