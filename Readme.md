[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.garyclayburg/upbanner-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.garyclayburg/upbanner-starter)
[![Build Status](https://travis-ci.org/gclayburg/upbanner.svg?branch=master)](https://travis-ci.org/gclayburg/upbanner)

# upbanner-starter

upbanner-starter adds a startup banner to any Spring Boot application to answer questions like:
- what app is running here?
- what URL?
- what version is it?

*Note*: upbanner-starter is not to be confused with the [standard spring boot banner](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-spring-application.html).  upbanner-starter executes only after the app has completely started and is listing on a TCP port.

# Compatibility
This project is designed to work with any Spring Boot 1.x or 2.x web application. It won't do anything until your project has started and is listening for requests on a HTTP(S) port.

# Using
Add this to pom.xml:
```xml
        <dependency>
            <groupId>com.garyclayburg</groupId>
            <artifactId>upbanner-starter</artifactId>
            <version>2.0.13</version>
        </dependency>
```
Or to build.gradle:

```groovy
    compile group: 'com.garyclayburg', name:'upbanner-starter', version: '2.0.13'
```
# example output:

```
2019-03-01 09:56:02.797  INFO 16029 --- [           main] com.garyclayburg.upbanner.WhatsUp        : 
----------------------------------------------------------------------------------------------------
    Application is UP!                            git.build.time:    null
    Local:     http://localhost:8070              git.build.version: null
    External:  http://127.0.1.1:8070              git.commit.id:     null
    Host:      http://gary-XPS-13-9360:8070       git.remote.origin: null
----------------------------------------------------------------------------------------------------
```

### example output with git.properties file in the classpath:

```
2019-03-01 09:54:41.688  INFO 15686 --- [           main] com.garyclayburg.upbanner.WhatsUp        : 
----------------------------------------------------------------------------------------------------
    Application is UP!                            git.build.time:    2019-03-01T09:52:34-0700
    Local:     http://localhost:8070              git.build.version: 0.0.2-SNAPSHOT
    External:  http://127.0.1.1:8070              git.commit.id:     10db5b227f40569993c99ac8b6b5fd48860f6496
    Host:      http://gary-XPS-13-9360:8070       git.remote.origin: Unknown
----------------------------------------------------------------------------------------------------
```
This git.properties is generated with a build plugin like this in maven builds:
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>pl.project13.maven</groupId>
                <artifactId>git-commit-id-plugin</artifactId>
            </plugin>
            ...
```
or if using gradle:
```
plugins {
    id 'com.gorylenko.gradle-git-properties' version '2.0.0'
}
```

## an https application with existing spring properties info.app.name and info.app.version:
[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)

```
2019-03-01 10:00:03.139  INFO 16529 --- [           main] com.garyclayburg.upbanner.WhatsUp        : 
----------------------------------------------------------------------------------------------------
    memuser:0.6.4 is UP!                          git.build.time:    2019-02-28T22:51-0700
    Local:     https://localhost:8443             git.build.version: 0.6.4
    External:  https://127.0.1.1:8443             git.commit.id:     cb1de93bfbc12a14bed6a7ba33dffe9656b0e1e1
    Host:      https://gary-XPS-13-9360:8443      git.remote.origin: ssh://git@scranton2:2233/home/git/memuser.git
----------------------------------------------------------------------------------------------------
2019-03-01 10:00:03.143  INFO 16529 --- [           main] c.g.memuser.MemuserApplication           : Started MemuserApplication in 6.357 seconds (JVM running for 7.438)

``` 

# Contributing

There are many other properties available at runtime from the runtime environment, spring build properties, spring externalized properties and more.  Maybe we need a  template mechanism for overriding the default layout?

Pull requests are welcome
