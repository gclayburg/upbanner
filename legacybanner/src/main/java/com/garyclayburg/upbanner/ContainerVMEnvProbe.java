package com.garyclayburg.upbanner;

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

    @ConditionalOnClass({oshi.SystemInfo.class})
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
}
