#!/bin/sh
# clean the target repository
mvn clean
# compile the code
mvn install -Dmaven.test.skip=true
# build jars
mvn package assembly:attached deploy -Dmaven.test.skip=true
# website generation
mvn site:site site:deploy 
