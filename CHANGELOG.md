# Changelog

## [Unreleased]

### Added

- CHANGELOG.md file

### Changes

- README.md updates

## [2.2.4] - 2021-4-12

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
