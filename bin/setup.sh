#!/usr/bin/env bash

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

SCRIPT_DIR=$(cd $(dirname $0); pwd)
yosegi_deliver_tool='yosegi_deliver'
hdfs_yosegi_lib='/yosegi_lib'
external_jar_files=(
  yosegi-tools
)

function show_usage() {
cat << EOS
$0 [-i yosegi_source] [-l hdfs_yosegi_lib]
gathering yosegi related jar files to setup environment.

Option: yosegi_source
not defined:   get the release version from maven repository.
versions file: get the version described in the versions file from maven repository.
               example versions file is bin/$yosegi_deliver_tool/versions.sh.template.
yosegi directory: get the jar files from yosegi directory located following repositories,
               - yosegi
               and create jar using "mvn package" at each directories.

Option: hdfs_yosegi_lib
  define yosegi libraly location in HDFS to create add_jar.hql .
  default value is "$hdfs_yosegi_lib" .
EOS
  exit 0
}


function create_libdir() {
  libdir="$SCRIPT_DIR/../jars"
  if [ -d $libdir ]; then rm -r $libdir; fi
  mkdir -p $libdir
  mkdir $libdir/yosegi
}


function get_external_jars() {
  local fn
  local not_found_files=()
  for fn in ${external_jar_files[@]}
  do
    local src_fn=$(find $SCRIPT_DIR/../target -name $fn-[0-9.]*.jar | sort | grep -v test | grep -v sources | grep -v javadoc)
    if [ ! -z "$src_fn" ]
    then cp $src_fn $libdir/$fn.jar
    else not_found_files+=($fn)
    fi
  done

  if [ ${#not_found_files[@]} != 0 ]; then
cat << EOS
Please execute 'mvn package' to create external jar files for command tools.
Following external jar files are not found in this dir.
EOS
    for fn in ${not_found_files[@]}
    do
      echo $fn
    done
    exit -1
  fi
}

function get_yosegi_jars() {
  local src=$1
  local hdfs_yosegi_lib=$2
  local local_jars=local

  local src_dn
  local yosegi_tool=$SCRIPT_DIR/$yosegi_deliver_tool
  local yosegi_jars=$yosegi_tool/jars/$local_jars
  $yosegi_tool/get_jar.sh get -i $src -l $hdfs_yosegi_lib -o $local_jars
  for src_dn in `find $yosegi_jars -type d -name latest`
  do
    local dist_dn=$(echo $src_dn | cut -b $((${#yosegi_jars}+2))- | cut -d/ -f1)
    mv $src_dn $libdir/yosegi/$dist_dn
  done
  cat $yosegi_jars/add_jar.hql | sed 's/latest\///' > $libdir/yosegi/add_jar.hql
  rm -r $yosegi_jars
}

function get_lib_jars() {
  local dependency_libdir="$libdir/lib"
  mkdir $dependency_libdir
  cp $SCRIPT_DIR/../etc/dependency_pom.xml.template $dependency_libdir/pom.xml
  mvn dependency:copy-dependencies -DoutputDirectory=. -f $dependency_libdir/pom.xml
  rm $dependency_libdir/pom.xml
}

function get_jars() {
  create_libdir
  get_external_jars
  get_yosegi_jars $*
  get_lib_jars
}


yosegi_source=$SCRIPT_DIR/yosegi_deliver/version.sh
while getopts i:l:h OPT
do
  case $OPT in
    i)  yosegi_source="$OPTARG" ;;
    l)  hdfs_yosegi_lib="$OPTARG" ;;
    h)  show_usage ;;
    \?) show_usage ;;
  esac
done
shift $((OPTIND - 1))

get_jars $yosegi_source $hdfs_yosegi_lib

