package com.garyclayburg.upbanner;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Creates a minimal {@link BuildProperties}  with {@link BuildPropertiesStub#buildProperties}
 * @author Gary Clayburg
 */
@Configuration
public class BuildPropertiesStub {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(BuildPropertiesStub.class);

    /**
     * Creates a minimal {@link BuildProperties} when {@code boot-info.properties} is not on the classpath.
     * <p>
     *     If {@code boot-info.properties} <b>is</b> on the classpath, then we expect Spring Boot will create a fully populated {@link BuildProperties} bean
     * @return minimal {@link BuildProperties}
     */
    @Conditional(ConditionalOnMissingBuildInfo.class)
    @Bean
    public BuildProperties buildProperties() {
        Properties p = new Properties();
        p.put("version", "development build");
        p.put("org.label-schema.build-date", "development");
        return new BuildProperties(p);
    }
}
