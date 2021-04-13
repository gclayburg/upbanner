[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.garyclayburg/upbanner-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.garyclayburg/upbanner-starter)
[![Build Status](https://travis-ci.org/gclayburg/upbanner.svg?branch=master)](https://travis-ci.org/gclayburg/upbanner)
[![javadoc](https://javadoc.io/badge2/com.garyclayburg/upbanner-starter/javadoc.svg)](https://javadoc.io/doc/com.garyclayburg/upbanner-starter)
# upbanner-starter

upbanner-starter adds a startup banner to any Spring Boot application.  It tries  to answer questions like:
- what app is running here?
- what URL?  http? https? port?
- what version?  embedded version #, git commit   
- what database is it using?

*Note*: upbanner-starter is not to be confused with the [standard spring boot banner](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-spring-application.html).  upbanner-starter executes only after the app has completely started and is listing on a TCP port.

# Using
Add this to pom.xml:
```xml
        <dependency>
            <groupId>com.garyclayburg</groupId>
            <artifactId>upbanner-starter</artifactId>
            <version>x.x.x</version>
        </dependency>
```
Or to build.gradle:

```groovy
    compile group: 'com.garyclayburg', name:'upbanner-starter', version: 'x.x.x'
```

The recommended version is shown in the badge at the top of this README.md

# example output:

This is a very basic web application that listens on standard port 8080. Any of the URLs shown can be used to access the app:

```
----------------------------------------------------------------------------------------------------
    WebJar244Application is UP!
    Local:      http://localhost:8080
    External:   http://127.0.1.1:8080
    Host:       http://gary-XPS-13-9360:8080
      Running on JVM: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 1.8.0_201
----------------------------------------------------------------------------------------------------
```

This is another basic app that is also configured to use Spring Data MongoDB
```
----------------------------------------------------------------------------------------------------
    Mongo244Application is UP!
    Local:      http://localhost:7349
    External:   http://127.0.1.1:7349
    Host:       http://gary-XPS-13-9360:7349
      Running on JVM: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 1.8.0_201
      Using MongoClient mongodb://localhost:27017/test
----------------------------------------------------------------------------------------------------
```

This app creates a git.properties file during the build, uses a custom mongo uri and a version number
```
----------------------------------------------------------------------------------------------------
    scimedit1:0.0.1-SNAPSHOT is UP!               git.commit.time:   2020-01-28T10:32:29-0700
    Local:      http://localhost:8050             git.build.version: 0.0.1-SNAPSHOT
    External:   http://127.0.1.1:8050             git.commit.id:     3f429cf1cd933897ce1cb3ed6179df371e3ac36b
    Host:       http://gary-XPS-13-9360:8050      git.remote.origin: ssh://git@scranton2:2233/home/git/scimedit1.git
    Running on JVM: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 1.8.0_201
      Using mongodb uri: mongodb://localhost:27017
----------------------------------------------------------------------------------------------------
```
This one is from an app that uses https. There is no version number since the app is running in an IDE.
```
----------------------------------------------------------------------------------------------------
    memuser is UP!
    Local:      https://localhost:8443
    External:   https://127.0.1.1:8443
    Host:       https://gary-XPS-13-9360:8443
      Running on JVM: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 1.8.0_201
      Using mongodb uri: mongodb://patonsynconsoleuser:xxxx@yale.garyclayburg.com:27017/patonsynconsoledb
----------------------------------------------------------------------------------------------------
```
This one is from running the same app as a spring boot jar file - but without https:
----------------------------------------------------------------------------------------------------
```
    memuser:0.8.1-SNAPSHOT is UP!                 git.commit.time:   2021-03-01T16:44-0700
    Local:      http://localhost:8080             git.build.version: 0.8.1-SNAPSHOT
    External:   http://127.0.1.1:8080             git.commit.id:     5c37e87b9808b38aacf61ea3213df08c30208650
    Host:       http://gary-XPS-13-9360:8080      git.remote.origin: ssh://git@scranton2:2233/home/git/memuser.git
      Running on JVM: Azul Systems, Inc. OpenJDK 64-Bit Server VM 1.8.0_282
      Using mongodb uri: mongodb://patonsynconsoleuser:xxxx@yale.garyclayburg.com:27017/patonsynconsoledb
----------------------------------------------------------------------------------------------------
```
The same app again, but this time running within a Docker container:
```
----------------------------------------------------------------------------------------------------
    memuser:0.8.1-SNAPSHOT is UP!                 git.commit.time:   2021-03-01T16:44-0700
    Local:      http://localhost:8080             git.build.version: 0.8.1-SNAPSHOT
    External:   http://172.17.0.2:8080            git.commit.id:     5c37e87b9808b38aacf61ea3213df08c30208650
    Docker:     http://53a3672a80a5:8080          git.remote.origin: ssh://git@scranton2:2233/home/git/memuser.git
      Running on JVM: Oracle Corporation OpenJDK 64-Bit Server VM 1.8.0_151
      Using mongodb uri: mongodb://patonsynconsoleuser:xxxx@yale.garyclayburg.com:27017/patonsynconsoledb
----------------------------------------------------------------------------------------------------
```

The hostname of the Docker: URL is the hostname of the docker container and will only be accessible to other containers running on the same network. The External: URL may be accessible from the host where this docker container is being run, depending on your OS. As with any docker container, you will still need to map this port to a port available on the host if you want to access this application from another host. Since upbanner runs as a dependency of your application, it has no visibility of what or any ports are mapped this way.

## git support

The git section in the banner is automatically shown when it finds a git.properties file in the classpath.  It can be generated during the build with a build plugin like this in maven builds:
```xml
    <plugin>
        <groupId>pl.project13.maven</groupId>
        <artifactId>git-commit-id-plugin</artifactId>
    </plugin>
```
or if using gradle:
```
plugins {
    id 'com.gorylenko.gradle-git-properties' version '2.x.x'
}

gitProperties {
    failOnNoGitDirectory = false
    keys = ["git.branch", "git.commit.id.abbrev", "git.commit.id.describe", "git.build.time", "git.commit.time","git.commit.id", "git.remote.origin", "git.build.version"]
}
```
More information about [generating git.properties in Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-git-info)


## Whats going on here

The information shown in the upbanner is gathered without any configuration.  Prior to displaying the banner, it will probe the environment to collect all the meta information it can about .

### How is the application name determined?
We probe the running application to determine the application name from a few well known places.  The first one that returns a value wins:
1. spring property "spring.application.name".
2. application name determined during spring boot application launch.  This is normally the name of the main application class used with SpringApplication.run(...).
3. A name generated by examining the property "java.sun.command" and "Start-Class" attribute from a running jar file manifest.
4. The generic name "Application".

More details can be found in the javadoc

### How is the version # determined?

We probe the running app for a few well-known places to find a version number.  The first one that returns a value wins: 

1. "info.app.version" spring external property
2. "git.build.version" from classpath file git.properties
3. "build.version" from classpath file build-info.properties
3. "Implementation-Version" spring boot jar file manifest entry

It is also quite possible that there is no information to be found in the running
application to determine the version number.  For example, when running in an IDE you 
might not have placed a git.properties file in the classpath, and you probably aren't
running from a Spring Boot jar.  In that case, the version is simply not printed. 
However, the version will show up if you do a complete build through maven or gradle and 
execute the production build.

# Properties
```
upbanner.debug=true
```
When true, show a complete dump of the environment in which this application is running.  This is false by default.  
```
upbanner.show-banner=true
```
When true, show the banner on startup.  This is true by default


# Startup debug

Having trouble starting up your Spring Boot app?  Want to know more about the environment where your app was really started?  If so, enable the debug flag as a runtime argument:
```
java -jar yourapp.jar --upbanner.debug=true
```
or as an environment variable:
```
UPBANNER_DEBUG=true; java -jar yourapp.jar
```
Running your app this way will dump out a large list of startup related facts about your application to the console.  This flag is not enabled by default.

We've all been there before. Sometimes your Spring Boot app fails to startup.  There are many things that are going on to prepare your app so that it is ready for use.  It's not always immediately obvious what thing failed.  Often, we ask ourselves things like, 
- Is my app getting the settings I think it should?  
- Is my app seeing the environment I need?
- Is it using the right JVM parameters?  
- Is it really picking up the latest snapshot version of a dependency? 
- What are the exact paths and version of a dependency?  
- Is this app running in an environment where memory is limited?  
- Is the CPU being throttled by my cloud provider? 
- Why is my app startup timing out?

And the list goes on and on.

Some of these questions can get complicated to answer.  There are different ways a Spring Boot app can be built, packaged and started.  This optional debug phase of upbanner tries to answer as many of these startup related issues as possible.  It can do this because it understands the nature of Spring boot applications and how it interacts with the underlying components such as the JVM, hardware, memory and more.  
Here is a short list of the concepts it knows about:
- the classpath of the app running in an IDE
- the classpath of the app packaged as a war or jar running via java -jar
- the classpath of the app running as a Spring Boot expanded jar or war
- the classpath of the app running via $ ./gradlew bootRun or $ mvn spring-boot:run
- boot-info.properties
- root jar file manifest
- snapshot dependencies jar file manifest
- JVM arguments
- JVM main class
- Spring boot Start-Class
- System environment
- System properties
- Linux cgroup limits
- JVM memory parameters

When debug is enabled, upbanner will dump everything to the console.  It tries to do this fairly early in the startup sequence.

It can also dump more details about the hardware this app is running on.  To add this information to the debug report, you need to add an extra dependency to your app:

```xml
		<dependency>
			<groupId>com.github.oshi</groupId>
			<artifactId>oshi-core</artifactId>
			<version>5.6.0</version>
		</dependency>

```

If this dependency is present in your classpath, debug will use this to show you more details about the OS and hardware where this JVM is running.  There is nothing additional to configure here.

# Customizing

If you want to override the banner produced with your own, create a Spring @Component that implements WhatsUpBanner.  For example:

```java
import javax.annotation.PostConstruct;

import com.garyclayburg.upbanner.WhatsUpBanner;
import com.garyclayburg.upbanner.WhatsUpProbes;
import org.springframework.stereotype.Component;

@Component
public class CustomWhatsUp implements WhatsUpBanner {

    private WhatsUpProbes whatsUpProbes;

    public CustomWhatsUp(WhatsUpProbes whatsUpProbes) {
        this.whatsUpProbes = whatsUpProbes;
    }

    @PostConstruct
    public void printDebugOnStartup() {
        whatsUpProbes.dumpAll();
    }

    @Override
    public void printBanner() {
        whatsUpProbes.registerUpContributor(stringBuilder -> stringBuilder
                .append("      last commit message is: ")
                .append(whatsUpProbes.getGitProperty("git.commit.message.short"))
                .append(System.lineSeparator()));
        whatsUpProbes.printDefaultBanner();
    }
}
```


# Design goals

## Overhead
This app is designed to have very little overhead.  It only does its work during application startup and does nothing when it is disabled.  By default upbanner.show-banner is enabled and  upbanner.debug is disabled.  The intention here is that you only need to enable upbanner.debug when there is some question or problem with your application startup.

## Don't break anything
Upbanner is intended to provide helpful information during the startup of any Spring Boot Application.  It should not throw exceptions.  It is no fun when a troubleshooting tool like this adds another startup problem.

## Compatibililty
This project is designed to work with any Spring Boot 1.x or 2.x application.  If the app is also a web app it will print an upbanner on successful startup.  

Any Spring Boot app can use the debug flag to troubleshoot application startup.

## Security and secret masking

The upbanner portion prints a summary of what has successfully started up.  As such, it is intended to be enabled permanently.  Items added to this banner should have any secrets masked out.  As an example, the MongoUpContributor will show what mongo database your app is using, but will not show the password.

The debug portion dumps out many things about the environment to the console.  In its current form, it does not attempt to mask or hide any secrets that might exist in the environment.  Because of this, the debug flag is not enabled by default.  It is intended to be used by enabling it manually to troubleshoot specific cases. 

# Contributing

There are many other properties available at runtime from the runtime environment, spring build properties, spring externalized properties and more.  Maybe we need a  template mechanism for overriding the default layout?

Pull requests are welcome
