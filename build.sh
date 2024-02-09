#!/bin/bash
java -version
export "SDKMAN_DIR=$HOME/.sdkman/"
source "$HOME/.sdkman/bin/sdkman-init.sh"
#sdk list java
sdk use java 17.0.10-tem
java -version
#sdk list java
echo "done with versions"
