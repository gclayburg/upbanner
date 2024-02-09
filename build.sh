#!/bin/bash
java -version

if [ -f "$HOME/.sdkman" ]; then  #standard SDK install location
	export SDKMAN_DIR="$HOME/.sdkman"
elif [ -f /usr/local/sdk/.sdkman ]; then  #jenkins build agent uses this
	export SDKMAN_DIR=/usr/local/sdk/.sdkman
fi
source "$SDKMAN_DIR/bin/sdkman-init.sh"
echo "now using sdkman I hope"
java -version
#sdk list java
sdk use java 17.0.10-tem
java -version
#sdk list java
echo "done with versions"
