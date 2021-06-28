package com.example.demo;

import javax.annotation.PostConstruct;

import java.util.Arrays;

import com.garyclayburg.upbanner.MongoUpContributor;
import com.garyclayburg.upbanner.WhatsUpBanner;
import com.garyclayburg.upbanner.WhatsUpProbes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2021-03-23 15:21
 *
 * @author Gary Clayburg
 */
@Component
public class MyWhatsUp implements WhatsUpBanner {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MyWhatsUp.class);
    private final WhatsUpProbes whatsUpProbes;
    private Environment environment;

    public MyWhatsUp(WhatsUpProbes whatsUpProbes, Environment environment) {
        this.whatsUpProbes = whatsUpProbes;
        this.environment = environment;
    }

    @Override
    public void printBanner() {
        //compact banner
        if (whatsUpProbes.isShowBanner()) {
            String gitCommitId = whatsUpProbes.getGitProperty("git.commit.id");
            log.info("\n\n    {} is UP at {} " +
                     (whatsUpProbes.isDocker() ? " in docker" : "") +
                     (gitCommitId != null ? " git: " + gitCommitId : ""),
                    whatsUpProbes.getAppNameVersion(), whatsUpProbes.getExternalURL());
        }
        //print the default banner 2 with additional lines
        whatsUpProbes.registerUpContributor(stringBuilder -> {
            stringBuilder.append("      using server port: ")
                    .append(whatsUpProbes.getEnvironmentPropertyPrintable("server.port"))
                    .append(System.lineSeparator());
            stringBuilder.append("      profile: ").append(Arrays.toString(environment.getActiveProfiles()))
                    .append(System.lineSeparator());
        });
        whatsUpProbes.printDefaultBanner();
        whatsUpProbes.getGitProperty(null);
        whatsUpProbes.getBuildProperty(null);
    }
}
