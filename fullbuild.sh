#!/bin/bash
if [ -f "$HOME/.sdkman" ]; then  #standard SDK install location
	export SDKMAN_DIR="$HOME/.sdkman"
elif [ -f /usr/local/sdk/.sdkman ]; then  #jenkins build agent uses this
	export SDKMAN_DIR=/usr/local/sdk/.sdkman
fi
source "$SDKMAN_DIR/bin/sdkman-init.sh"

sdk use java 17.0.10-tem
mvn clean javadoc:jar source:jar install
exitcode=$?
if [ $exitcode -ne 0 ]; then
  exit $exitcode
fi
sdk use java 8.0.402-tem
mvn install

