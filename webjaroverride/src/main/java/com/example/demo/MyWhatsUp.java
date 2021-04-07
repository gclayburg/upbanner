package com.example.demo;

import javax.annotation.PostConstruct;

import com.garyclayburg.upbanner.MongoUpContributor;
import com.garyclayburg.upbanner.WhatsUpBanner;
import com.garyclayburg.upbanner.WhatsUpProbes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext context;

    public MyWhatsUp(WhatsUpProbes whatsUpProbes, ApplicationContext context) {
        this.whatsUpProbes = whatsUpProbes;
        this.context = context;
    }

    @PostConstruct
    public void printDebugOnStartup() {
        whatsUpProbes.dumpAll(stringBuilder -> stringBuilder
                .append("  working directory: ")
                .append(whatsUpProbes.getEnvironmentPropertyPrintable("PWD"))
                .append(System.lineSeparator()));
    }

    @Override
    public void printBanner() {
        if (whatsUpProbes.isShowBanner()) {

            String gitCommitId = whatsUpProbes.getGitProperty("git.commit.id");
            log.info("\n\n    {} is UP at {} " +
                     (whatsUpProbes.isDocker() ? " in docker" : "") +
                     (gitCommitId != null ? " git: " + gitCommitId : ""),
                    whatsUpProbes.getAppNameVersion(), whatsUpProbes.getExternalURL());
        }
        whatsUpProbes.registerUpContributor(new MongoUpContributor(whatsUpProbes, context));

        whatsUpProbes.printHostPortVersionGitBanner(stringBuilder -> stringBuilder.append("      using server port: ").append(whatsUpProbes.getEnvironmentPropertyPrintable("server.port")).append(System.lineSeparator()));

    }
}
