
#!/bin/sh

yum remove -y maven || true

export VER="3.8.1"

# curl -O http://www-eu.apache.org/dist/maven/maven-3/${VER}/binaries/apache-maven-${VER}-bin.tar.gz
curl -O https://mirrors.gigenet.com/apache/maven/maven-3/${VER}/binaries/apache-maven-${VER}-bin.tar.gz

tar xvf apache-maven-${VER}-bin.tar.gz

sudo mv apache-maven-${VER} /opt/maven

cat <<EOF | sudo tee /etc/profile.d/maven.sh
export MAVEN_HOME=/opt/maven
export PATH=\$PATH:\$MAVEN_HOME/bin
EOF


source /etc/profile.d/maven.sh


cat <<EOF | sudo tee -a ~/.bashrc
source /etc/profile.d/maven.sh
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.11.0.9-1.el7_9.x86_64

EOF


source ~/.bashrc

mvn --version
