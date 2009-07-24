#!/bin/sh
# mvn repo and website generation
mvn clean compile deploy site:site site:deploy -Dmaven.test.skip=true
# transfer to intranet
scp -r ./target/publish/* cprudhom@x-info.emn.fr:/rrs.fs/x-info/choco-solver/publish/ws
