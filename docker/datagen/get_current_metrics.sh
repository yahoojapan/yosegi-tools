#!/bin/sh

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

result=`sar -r 1 1 |tail -2|head -n 1`
dt=`echo $result|awk {'print $1'}`
kbmemfree=`echo $result|awk {'print $2'}`
kbmemused=`echo $result|awk {'print $3'}`
memused=`echo $result|awk {'print $4'}`
kbbuffers=`echo $result|awk {'print $5'}`
kbcached=`echo $result|awk {'print $6'}`
kbcommit=`echo $result|awk {'print $7'}`
commit=`echo $result|awk {'print $8'}`
kbactive=`echo $result|awk {'print $9'}`
kbinact=`echo $result|awk {'print $10'}`
kbdirty=`echo $result|awk {'print $11'}`

cat <<__JSON__
{"dt":"$dt","kbmemfree":$kbmemfree,"kbmemused":$kbmemused,"memused":$memused,"kbbuffers":$kbbuffers,"kbcached":$kbcached,"kbcommit":$kbcommit,"commit":$commit,"kbactive":$kbactive,"kbinact":$kbinact,"kbdirty":$kbdirty}
__JSON__
