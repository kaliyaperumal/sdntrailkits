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



[INFO] Archetype repository not defined. Using the one from [org.onosproject:onos-bundle-archetype:1.7.1] found in catalog remote
Define value for property 'groupId': : com.verizon.onos.app
Define value for property 'artifactId': : verizon-app
Define value for property 'version':  1.0-SNAPSHOT: : 1.5.0
Define value for property 'package':  com.verizon.onos.app: : 
Confirm properties configuration:
groupId: com.verizon.onos.app
artifactId: verizon-app
version: 1.5.0
package: com.verizon.onos.app
 Y: : Y
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: onos-bundle-archetype:1.5.0
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: com.verizon.onos.app
[INFO] Parameter: artifactId, Value: verizon-app
[INFO] Parameter: version, Value: 1.5.0
[INFO] Parameter: package, Value: com.verizon.onos.app
[INFO] Parameter: packageInPathFormat, Value: com/verizon/onos/app
[INFO] Parameter: package, Value: com.verizon.onos.app
[INFO] Parameter: version, Value: 1.5.0
[INFO] Parameter: groupId, Value: com.verizon.onos.app
[INFO] Parameter: artifactId, Value: verizon-app
[INFO] project created from Archetype in dir: /home/verizon/onos/ONOS/onos/verizon-app
