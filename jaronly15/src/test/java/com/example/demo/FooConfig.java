package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br><br>
 * Created 2021-03-25 00:02
 *
 * @author Gary Clayburg
 */
@Configuration
public class FooConfig {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FooConfig.class);

    @Bean
    public FooService fooService() {
        log.debug("constructing foo");
        return new FooServiceImpl();
    }
}
