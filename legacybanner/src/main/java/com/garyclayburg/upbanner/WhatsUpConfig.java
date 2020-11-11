package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
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

    @Autowired
    public WhatsUpConfig(Environment environment, BuildProperties buildProperties, UpbannerSettings upbannerSettings) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.upbannerSettings = upbannerSettings;
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractWhatsUp whatsUp() {
        return new WhatsUp(environment, buildProperties, upbannerSettings);
    }
}
