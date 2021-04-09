package com.garyclayburg.upbanner;

import com.mongodb.client.MongoClient;
import com.mongodb.connection.ServerDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * <br><br>
 * Created 2021-04-08 21:30
 *
 * @author Gary Clayburg
 */
@Import(value = {
        WhatsUpConfig.class,
        ContainerVMEnvProbe.class,
        UpbannerSettings.class})
@ConditionalOnClass(name = "com.mongodb.client.MongoClient")
@Component("mongoClientSyncUpContributor")
public class MongoClientSyncUpContributor implements ExtraLinePrinter {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MongoClientSyncUpContributor.class);
    private ApplicationContext context;

    public MongoClientSyncUpContributor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void call(StringBuilder stringBuilder) {
        String[] beanNamesForType = context.getBeanNamesForType(MongoClient.class);
        if (beanNamesForType.length > 0) {
            MongoClient mongoClient = context.getBean(MongoClient.class);
            createMongoClientUpLine(stringBuilder, mongoClient);
        }
    }

    private void createMongoClientUpLine(StringBuilder stringBuilder, MongoClient mongoClient) {
        if (mongoClient.getClusterDescription().getServerDescriptions().size() > 0) {
            ServerDescription serverDescription = mongoClient.getClusterDescription().getServerDescriptions().get(0);
            //credentials not accessible from MongoClient.
            // we just assume its unsecured if we have gotten this far in our sequence of Mongo Probes
            // The default case of using a spring boot app with spring data mongodb which includes
            // a recent Mongo driver will arrive here.  Spring will create a default, unsecured
            // MongoClient.  This case allows us to print to the console where this client is pointing to
            stringBuilder.append("      using MongoClient mongodb://")
                    .append(serverDescription.getAddress().getHost())
                    .append(":")
                    .append(serverDescription.getAddress().getPort())
                    .append("/").append(getDatabaseName())
                    .append(System.lineSeparator());
        }
    }

    private String getDatabaseName() {
        String databaseName = "";
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
}
