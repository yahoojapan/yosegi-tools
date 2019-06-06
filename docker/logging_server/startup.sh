#!/usr/bin/env /bin/sh

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

archive_dir=${ARCHIVE_DIR:-/tmp/archive}

cat <<__CONF__ > setting.yaml

server:
  port: 8080

headers:
  - header:
    name: "timemills"
    class: "jp.co.yahoo.yosegi.tools.logging.server.header.CurrentTimeMillsHeaderPlugin"

loggers:
  - logger:
    name: "logback"
    class: "jp.co.yahoo.yosegi.tools.logging.server.logger.LogbackLogger"
    options:
      rotate_file_pattern: "${archive_dir}/archive.%d{yyyy-MM-dd-HH-mm}.yosegi"

servlets:
  - class: "jp.co.yahoo.yosegi.tools.logging.server.servlet.SimpleJsonLoggerServlet"
    path: "/logging"
    loggers:
      - "logback"
    headers:
      - "timemills"

__CONF__

java \
-server \
-Xms1g -Xmx1g -Xmn1g \
-XX:+UseG1GC \
-jar server.jar setting.yaml
