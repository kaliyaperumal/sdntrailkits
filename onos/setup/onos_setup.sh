export JAVA_HOME=/usr/lib/jvm/java-8-oracle/
export PATH=$JAVA_HOME/bin:$PATH
export ONOS_ROOT=/home/training/ONOS/onos1.5/onos
export MAVEN=/home/training/ONOS/apache-maven-3.3.9
export KARAF_VERSION=3.0.5
export KARAF_ROOT=/home/training/ONOS/apache-karaf-3.0.5
cd /home/training/ONOS/onos1.5/onos
source tools/dev/bash_profile
export ONOS_IP=10.76.190.84
#mvn clean install -DskipTests -Dcheckstyle.skip
#cat out.txt  | python -m json.tool > out.pretty

