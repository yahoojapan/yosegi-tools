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

package jp.co.yahoo.yosegi.tools.logging.server.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public final class YamlObjectUtils {

  private YamlObjectUtils() {}

  public static Map getMap( final Object target ) {
    if ( target == null || ! ( target instanceof Map ) ) {
      return new HashMap<Object,Object>();
    }
    return (Map)target;
  }

  public static List getList( final Object target ) {
    if ( target == null || ! ( target instanceof List ) ) {
      return new ArrayList<Object>();
    }
    return (List)target;
  }

  public static String getString( final Object target , final String defaultValue ) {
    if ( target == null ) {
      return defaultValue;
    }
    return target.toString();
  }

  public static int getInt( final Object target , final int defaultValue ) {
    if ( target == null ) {
      return defaultValue;
    }
    try {
      return Integer.parseInt( target.toString() );
    } catch ( NumberFormatException e ) {
      return defaultValue;
    }
  }

  public static long getLong( final Object target , final long defaultValue ) {
    if ( target == null ) {
      return defaultValue;
    }
    try {
      return Long.parseLong( target.toString() );
    } catch ( NumberFormatException e ) {
      return defaultValue;
    }
  }

  public static double getDouble( final Object target , final double defaultValue ) {
    if ( target == null ) {
      return defaultValue;
    }
    try {
      return Double.parseDouble( target.toString() );
    } catch ( NumberFormatException e ) {
      return defaultValue;
    }
  }

  public boolean getBoolean( final Object target , final boolean defaultValue ) {
    if ( target == null || ! ( target instanceof Boolean ) ) {
      return defaultValue;
    }
    return ( (Boolean)target ).booleanValue();
  }

}
