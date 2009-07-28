#!/bin/sh
# clean the target repository
mvn clean
# compile the code
mvn install -Dmaven.test.skip=true
# build jars
mvn deploy 
# website generation
mvn site:site site:deploy 
# transfer to intranet
scp -r ./target/publish/* cprudhom@x-info.emn.fr:/rrs.fs/x-info/choco-solver/publish
