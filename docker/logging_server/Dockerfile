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

FROM alpine:latest as build

RUN apk add openjdk8 maven

COPY source /opt/source
RUN cd /opt/source && mvn clean package

FROM alpine:latest 
MAINTAINER yosegi

# SETUP COMMAND
COPY startup.sh /startup.sh
RUN chmod 700 /startup.sh

# Install java
RUN apk add openjdk8 

# Install server
COPY --from=build /opt/source/target/simple-server-0.0.1-jar-with-dependencies.jar /server.jar

CMD ["/startup.sh"]
