package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link SpringBootCondition} that only matches when the specific resource {@code META-INF/build-info.properties} is not found in the classpath.
 *
 * @author Gary Clayburg
 */
public class ConditionalOnMissingBuildInfo extends SpringBootCondition {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ConditionalOnMissingBuildInfo.class);
    private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String location = "${spring.info.build.location:classpath:META-INF/build-info.properties}";
        String resource = context.getEnvironment().resolvePlaceholders(location);
        ResourceLoader loader = context.getResourceLoader() == null
                ? this.defaultResourceLoader : context.getResourceLoader();
        if ( loader.getResource(resource).exists()){
            return ConditionOutcome.noMatch("build-info.properties found on classpath");
        } else{
            return ConditionOutcome.match("build-info.properties not found on classpath");
        }
    }
}
