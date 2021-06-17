#!/bin/sh

yum remove -y maven || true

export VER="3.6.1"

curl -O http://www-eu.apache.org/dist/maven/maven-3/${VER}/binaries/apache-maven-${VER}-bin.tar.gz

tar xvf apache-maven-${VER}-bin.tar.gz

sudo mv apache-maven-${VER} /opt/maven

cat <<EOF | sudo tee /etc/profile.d/maven.sh
export MAVEN_HOME=/opt/maven
export PATH=\$PATH:\$MAVEN_HOME/bin
EOF


source /etc/profile.d/maven.sh


mvn --version
