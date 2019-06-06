# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM centos:centos7 as hive-base

RUN yum -y install wget java-1.8.0-openjdk maven

RUN wget http://ftp.jaist.ac.jp/pub/apache/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz
RUN tar xvf hadoop-3.2.0.tar.gz
RUN mv hadoop-3.2.0 /opt/hadoop
RUN rm -rf /opt/hadoop/share/doc

RUN wget http://ftp.jaist.ac.jp/pub/apache/hive/hive-3.1.1/apache-hive-3.1.1-bin.tar.gz
RUN tar xvf apache-hive-3.1.1-bin.tar.gz
RUN mv apache-hive-3.1.1-bin /opt/hive

RUN wget http://ftp.yz.yamagata-u.ac.jp/pub/network/apache/db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.tar.gz
RUN tar xvfz db-derby-10.14.2.0-bin.tar.gz
RUN mv db-derby-10.14.2.0-bin /opt/db-derby

COPY dependency_pom.xml dependency_pom.xml
RUN mvn dependency:copy-dependencies -DoutputDirectory=yosegi_lib -f dependency_pom.xml
RUN cp yosegi_lib/* /opt/hive/lib/

FROM centos:centos7
MAINTAINER yosegi

ENV PATH=$PATH:PATH=$PATH:/opt/hadoop/bin
ENV PATH=$PATH:PATH=$PATH:/opt/hive/bin
ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive
ENV DERBY_HOME=/opt/db-derby
ENV PATH=$PATH:$DERBY_HOME/bin
ENV CLASSPATH=$CLASSPATH:$DERBY_HOME/lib/derby.jar:$DERBY_HOME/lib/derbytools.jar

COPY --from=hive-base /opt/hadoop /opt/hadoop
COPY --from=hive-base /opt/hive /opt/hive
COPY --from=hive-base /opt/db-derby /opt/db-derby
COPY example_yosegi.hql example_yosegi.hql

RUN yum install -y java-1.8.0-openjdk
RUN echo 'export JAVA_HOME="$(ls -d /usr/lib/jvm/java-1.8.0-openjdk-*)/jre"' >> ~/.bashrc 

RUN source ~/.bashrc && schematool -dbType derby -initSchema && hive -f example_yosegi.hql
