package com.garyclayburg.upbanner;

import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;

/**
 * <br><br>
 * Created 2021-04-07 08:55
 *
 * @author Gary Clayburg
 */
public class MongoUpContributor implements ExtraLinePrinter {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MongoUpContributor.class);
    private ApplicationContext context;
    private WhatsUpProbes whatsUpProbes;

    public MongoUpContributor(WhatsUpProbes whatsUpProbes, ApplicationContext context) {
        this.context = context;
        this.whatsUpProbes = whatsUpProbes;
    }

    @Override
    public void call(StringBuilder stringBuilder) {
        showFullDBuri(stringBuilder);
        if (context != null) {
            //allow for context to be null when running some unit tests
            //todo refactor out some of the concerns in WhatsUpProbes, i.e. areas that need a context
            //     and those that do not
            probeMongoClient(stringBuilder);
        }
    }

    private void probeMongoClient(StringBuilder stringBuilder) {
        try {
            Class mongoClientExists = this.getClass().getClassLoader().loadClass("com.mongodb.MongoClient");
            String[] beanNamesForType = context.getBeanNamesForType(MongoClient.class);
            if (beanNamesForType.length > 0) {
                MongoClient mongoClient = context.getBean(MongoClient.class);
                createMongoClientLine(stringBuilder, mongoClient);
            }
        } catch (ClassNotFoundException ignored) {
            log.info("MongoClient not found");
        }
    }

    static void createMongoClientLine(StringBuilder stringBuilder, MongoClient mongoClient) {
        ServerAddress mongoClientAddress = mongoClient.getAddress();
        String username = "";
        if (mongoClientAddress != null) {
            List<MongoCredential> credentialsList = mongoClient.getCredentialsList();
            if (credentialsList.size() > 0) {
                username = credentialsList.get(0).getUserName() + ":xxxx@";
            }
            stringBuilder.append("    uses MongoClient: mongodb://")
                    .append(username)
                    .append(mongoClientAddress.getHost())
                    .append(":").append(mongoClientAddress.getPort())
                    .append("/").append("somedb");
        }
    }

    private void showFullDBuri(StringBuilder stringbuilder) {
        String mongoDBuri = whatsUpProbes.getEnvironmentProperty("spring.data.mongodb.uri");
        if (mongoDBuri != null) {
            stringbuilder
                    .append("    using mongodb uri: ")
                    .append(maskSecrets(mongoDBuri))
                    .append(System.lineSeparator());
        } else {
            if (whatsUpProbes.getEnvironmentProperty("spring.data.mongodb.host") != null ||
                whatsUpProbes.getEnvironmentProperty("spring.data.mongodb.port") != null) {
                // app is using individual properties to configure mongodb connection, instead of
                // just a single spring.data.mongodb.uri
                stringbuilder.append("    using mongodb://");
                if (whatsUpProbes.getEnvironmentProperty("spring.data.mongodb.username") != null) {
                    stringbuilder.append(whatsUpProbes.getEnvironmentPropertyPrintable("spring.data.mongodb.username"))
                            .append("@");
                }
                stringbuilder.append(whatsUpProbes.getEnvironmentPropertyPrintable("spring.data.mongodb.host"))
                        .append(":")
                        .append(whatsUpProbes.getEnvironmentPropertyPrintable("spring.data.mongodb.port"));
                if (whatsUpProbes.getEnvironmentProperty("spring.data.mongodb.database") != null) {
                    stringbuilder.append("/")
                            .append(whatsUpProbes.getEnvironmentPropertyPrintable("spring.data.mongodb.database"));
                }
                stringbuilder.append(" (inferred from properties)").append(System.lineSeparator());
            } else {
                String databaseName = "";
                databaseName = getDatabaseName(databaseName);
                if (whatsUpProbes.getEnvironmentProperty("local.mongo.port") != null) {
                    stringbuilder.append("    using embedded mongodb://localhost:")
                            .append(whatsUpProbes.getEnvironmentPropertyPrintable("local.mongo.port"))
                            .append("/").append(databaseName)
                            .append(System.lineSeparator());
                }
            }
        }
    }

    private String getDatabaseName(String databaseName) {
        try {
            String[] beanNamesForType = context.getBeanNamesForType(MongoProperties.class);
            if (beanNamesForType.length > 0) {
                MongoProperties mongoProperties = context.getBean(MongoProperties.class);
                databaseName = mongoProperties.getMongoClientDatabase();
            }
        } catch (BeansException ignored) {
        }
        return databaseName;
    }

    static String maskSecrets(String environmentProperty) {
        //e.g. mongodb://patonsynconsoleuser:monkey123@yale.garyclayburg.com:27017/patonsynconsoledb
        return environmentProperty.replaceAll("mongodb://(.*):(.*)@", "mongodb://$1:xxxx@");
    }
}
