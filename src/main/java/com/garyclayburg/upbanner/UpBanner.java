package com.garyclayburg.upbanner;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Prints UP banner to stdout on Spring Boot application startup.  Build properties from
 * {@code META-INF/boot-info.properties} are used to show relevant info about this app.
 * <p>Example output:<p>
 * <pre>
 2018-02-28 11:40:24.534  INFO 24276 --- [           main] com.garyclayburg.upbanner.UpBanner       :
 ----------------------------------------------------------------------------------------------------
     LDAP-SCIM gateway:0.2.0-SNAPSHOT is UP!       build-date: 2018-02-28T11:40:06.591-07:00[America/Denver]
     Local:     http://localhost:8002              vcs-ref: 2e88528b28361da51eea96d5840fcc737281e7f4
     External:  http://127.0.1.1:8002              vcs-url: https://github.com/gclayburg/scimldap
                                                   description: [git: 4 staged added, 53 unstaged added, 5 unstaged modified] ldap gateway
 ----------------------------------------------------------------------------------------------------

 * </pre>
 *
 * @author Gary Clayburg
 */
@Component
public class UpBanner implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UpBanner.class);

    private final Environment environment;
    private final BuildProperties buildProperties;

    public UpBanner(Environment environment, BuildProperties buildProperties) {
        this.environment = environment;
        this.buildProperties = buildProperties;
    }

    @Override
    public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
        int localPort = event.getEmbeddedServletContainer().getPort();
        String hostAddress;
        String hostName;
        try{
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostAddress = "unknown host";
            hostName = "unknown host";
        }
        printVersion(hostAddress,hostName,localPort);
    }

    public void printVersion(String hostAddress,String hostName,int localPort) {
        String proto = deduceProtocol();
        String banner = "\n----------------------------------------------------------------------------------------------------\n";
        String c1r1 = String.format("%s is UP!", deduceAppNameVersion() );
        String c1r2 = String.format("Local:     %s://localhost:%s", proto,localPort);
        String c1r3 = String.format("External:  %s://%s:%s ", proto, hostAddress, localPort);
        String c1r4 = String.format("Host:      %s://%s:%s ", proto, hostName, localPort);
        String c2r1 = String.format("build-date: %s", getBuildProp("org.label-schema.build-date"));
        String c2r2 = String.format("vcs-ref: %s", getBuildProp("org.label-schema.vcs-ref"));
        String c2r3 = String.format("vcs-url: %s", getBuildProp("org.label-schema.vcs-url"));
        String c2r4 = String.format("description: %s", getBuildProp("org.label-schema.description"));
        banner += String.format("    %-45s %s\n", c1r1, c2r1);
        banner += String.format("    %-45s %s\n", c1r2, c2r2);
        banner += String.format("    %-45s %s\n", c1r3, c2r3);
        banner += String.format("    %-45s %s\n", c1r4, c2r4);
        banner += "----------------------------------------------------------------------------------------------------";
        log.info(banner);
    }

    private String deduceProtocol() {
        String retval = "http";
        String secure = getEnvProperty("security.require-ssl");
        if (secure != null && secure.equals("true")){
            retval = "https";
        }
        return retval;
    }

    private String deduceAppNameVersion() {
        String name = getEnvProperty("spring.application.name");
        if (name == null){
            name = "Application";
        }
        String version = getEnvProperty("info.app.version");
        if (version != null ){
            name +=":"+ version;
        }
        return name;
    }

    private String getBuildProp(String key){
        String retval = "unknown";
        if (buildProperties.get(key) != null){
            retval = buildProperties.get(key);
        }
        return retval;
    }
    private String getEnvProperty(String key) {
        try {
            return environment.getProperty(key);
        } catch (IllegalArgumentException ignored) { //i.e. it may need to be filtered first through build.gradle
            return "";
        }
    }

}
