# Changelog

## [2.3.1] - 2021-6-23
Bug fix release for compatibility with Spring Boot Devtools

### Added
- new flag to force upbanner to re-probe the environment during application restart ([#5][i5])

### Fixed
- An app could fail to start when it also uses spring-boot-devtools. ([#5][i5])

## [2.3.0] - 2021-4-23
upbanner.debug phase improvements

### Added

- debug: now show all enumerable properties available to the application and their source location
- debug: new comprehensive external property override report.  For example, this application has `server.port` defined in 2 places:

```
--- properties OVERRIDDEN report ---
java.home=/usr/lib/jvm/java-8-oracle/jre (from environment)
  java.home=/usr/lib/jvm/java-8-oracle/jre (from configurationProperties)
    java.home=/usr/lib/jvm/java-8-oracle/jre (from systemProperties)
      java.home=/usr/lib/jvm/java-8-oracle (from systemEnvironment)
server.port=8040 (from environment)
  server.port=8040 (from configurationProperties)
    server.port=8040 (from systemProperties)
      server.port=8050 (from applicationConfig: [classpath:/config/application-dev.yml])

```

### Changes

- debug: phase now occurs before any beans are constructed during startup sequence
- debug: system environment and properties are now printed alongside other properties, with their source location
- debug: all property names are now printed in sorted order
- debug: @PostConstruct method is no longer needed if you want to customize upbanner.  The debug phase is now invoked automatically before application beans are even constructed. See [Readme.md Customizing](Readme.md#customizing)

## [2.2.5] - 2021-4-14

Startup fail bug fix release

### Added

- CHANGELOG.md file

### Changed

- README.md updates

### Fixed

- fix error where application startup would fail if debug was enabled and OSHI was installed and certain OS libraries were not installed, e.g. application packaged in docker image based on alpine linux

## [2.2.4] - 2021-4-12

Expanded debug probes

### Added
- new probe to examine Spring Boot Jar file/ War file
- new probe to examine Spring Boot expanded Jar
- new probe to examine regular jar file dependencies
- new integration of optional OSHI hardware probe
- new UpContributor mechanism to expand and customize upbanner
- bundled MongoUpContributor to show the current DB in use
- many more integration test modules that demonstrate usage
- added SpringApplicationRunListener to capture name of running app
- added probe to detect Linux cgroup limitations in place for running app
- added probe to show JVM Memory limits in place for running app
- expanded locations to probe for current version of running app
- added docker detection
- added kubernetes detection

### Changed
- expanded customization options with WhatsUpBanner interface
- expanded Javadoc
- expanded README.md documentation
- upbanner.show-env setting name changed to upbanner.debug

## 2.1.1 - 2020-6-10

### Added
- add Apache 2.0 license
- added customization ability for WhatsUp
- show git.build.version
- added settings upbanner.show-env and upbanner.show-banner

## [2.0.13] - 2019-3-4
Initial public release

### Added
- Added port listener for Spring Boot 1.x
- Added port listener for Spring Boot 2.x
- Added http and https detection
- Added rudimentary version detection
- Added app name detection based on spring.application.name
- Added display of git commit information, if present
- Added display of org.label-schema build labels, if present

[i5]: https://github.com/gclayburg/upbanner/issues/5
