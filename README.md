<!---
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

# Yosegi Hive
This is [Yosegi](https://github.com/yahoojapan/yosegi) tools.

# License
This project is on the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
Please treat this project under this license.

# How do I get started?

CLI is a Command Line Interface tool for using Yosegi.
following tools are provided.

* bin/setup.sh # for gathering Yosegi related jars
* bin/yosegi.sh   # create yosegi data, and show data

yosegi.sh needs some jars, so please create jar files before using.

    $ mvn package

For preparation, get Yosegi jars and store then to proper directories.

    $ bin/setup.sh # get Yosegi jars from Maven repository (bin/setup.sh -h for help)

convert JSON data to MDS format.

    $ bin/yosegi.sh create -i etc/sample_json.txt -f json -o /tmp/sample.yosegi
    $ bin/yosegi.sh cat -i /tmp/sample.yosegi -o '-' # show whole data
    {"summary":{"total_price":550,"total_weight":412},"number":5,"price":110,"name":"apple","class":"fruits"}
    {"summary":{"total_price":800,"total_weight":600},"number":10,"price":80,"name":"orange","class":"fruits"}
    $ bin/yosegi.sh cat -i /tmp/sample.yosegi -o '-' -p '[ ["name"] ]' # show part of data
    {"name":"apple"}
    {"name":"orange"}

The tool has various functions.
Please see the [command list](docs/command_list.md) for details.

# How to contribute
We welcome to join this project widely.

## Maven
Yosegi Hive sources can get from the Maven repository.

## Compile sources
Compile each source following instructions.

    $ mvn clean package
