#!/bin/bash
java -version

if [ -f "$HOME/.sdkman" ]; then  #standard SDK install location
	export SDKMAN_DIR="$HOME/.sdkman"
elif [ -f /usr/local/sdk/.sdkman ]; then  #jenkins build agent uses this
	export SDKMAN_DIR=/usr/local/sdk/.sdkman
fi
source "$SDKMAN_DIR/bin/sdkman-init.sh"
java -version
echo "JAVA_HOME is $JAVA_HOME"
#sdk list java
#sdk use java 17.0.10-tem
#java -version
#echo "JAVA_HOME is $JAVA_HOME"
#sdk list java
#echo "done with versions"
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

# Check if the Java version starts with "1.8" (Java 8)
if [[ "$java_version" == 1.8* ]]; then
    echo "Java version is Java 8: $java_version"
    mvn install # should be running on jdk 8 and maven will only only be running those modules that are explicitly activated when running on java 8
else
    echo "Java version is not Java 8. Current version: $java_version"
    exit 1
fi
