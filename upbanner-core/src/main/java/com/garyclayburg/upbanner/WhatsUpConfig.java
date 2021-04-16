package com.garyclayburg.upbanner;

import com.garyclayburg.upbanner.jarprobe.JarProbe;
import com.garyclayburg.upbanner.oshiprobe.OshiProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * <br><br>
 * Created 2020-06-09 10:21
 *
 * @author Gary Clayburg
 */
@Configuration
public class WhatsUpConfig {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(WhatsUpConfig.class);

    private final Environment environment;
    private final BuildProperties buildProperties;
    private final UpbannerSettings upbannerSettings;
    private final JarProbe jarProbe;
    private ApplicationContext context;
    private OshiProbe oshiProbe;

    @Autowired
    public WhatsUpConfig(Environment environment, BuildProperties buildProperties, UpbannerSettings upbannerSettings, OshiProbe oshiProbe, JarProbe jarProbe, ApplicationContext context) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.upbannerSettings = upbannerSettings;
        this.oshiProbe = oshiProbe;
        this.jarProbe = jarProbe;
        this.context = context;
    }

    @Bean
    public WhatsUpProbes whatsUpProbes() {
        DebugDumper debugDumper = DebugDumper.getInstance();
        return debugDumper.getWhatsUpProbes();
    }

    @Bean
    @ConditionalOnMissingBean
    public WhatsUpBanner whatsUpBanner() {
        return new WhatsUp(whatsUpProbes());
    }
}
