/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.yahoo.yosegi.tools.logging.server.logger;

import java.io.IOException;
import java.util.Map;

public class Loggers {

  private final ILogger[] loggers;

  public Loggers( final ILogger[] loggers ) {
    this.loggers = loggers;
  }

  public boolean logging2( final byte[] message ) throws IOException {
    for ( int i = 0 ; i < loggers.length ; i++ ) {
      if( loggers[i].logging( message ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean[] logging( final byte[] message ) throws IOException {
    boolean[] result = new boolean[loggers.length];
    for ( int i = 0 ; i < result.length ; i++ ) {
      result[i] = loggers[i].logging( message );
    }
    return result;
  }

}
