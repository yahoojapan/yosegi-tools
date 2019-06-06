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

package jp.co.yahoo.yosegi.tools.logging.server;

import jp.co.yahoo.yosegi.tools.logging.server.config.YamlObjectUtils;
import jp.co.yahoo.yosegi.tools.logging.server.logger.DummyLogger;
import jp.co.yahoo.yosegi.tools.logging.server.logger.FindLogger;
import jp.co.yahoo.yosegi.tools.logging.server.logger.ILogger;
import jp.co.yahoo.yosegi.tools.logging.server.logger.Loggers;
import jp.co.yahoo.yosegi.tools.logging.server.header.FindHeaderPlugin;
import jp.co.yahoo.yosegi.tools.logging.server.header.HeaderProcess;
import jp.co.yahoo.yosegi.tools.logging.server.header.IHeaderPlugin;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.AbstractLoggerServlet;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.DummyLoggerServlet;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.FindServlet;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.ServletAndPath;

import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public final class Setting {

  public static Server createServer( final Map setting ) {
    Map serverSetting = YamlObjectUtils.getMap( setting.get( "server" ) );
    int port = YamlObjectUtils.getInt( serverSetting.get( "port" ) , 8080 );
    Server server = new Server( port );
    return server;
  }

  public static Map<String,IHeaderPlugin> createHeaderPluginMapping( final Map setting ) throws IOException {
    List headerSettingList = YamlObjectUtils.getList( setting.get( "headers" ) );

    Map<String,IHeaderPlugin> result = new HashMap<String,IHeaderPlugin>();
    for ( int i = 0 ; i < headerSettingList.size() ; i++ ) {
      Map headerSetting = YamlObjectUtils.getMap( headerSettingList.get( i ) );
      String headerName = YamlObjectUtils.getString( headerSetting.get( "name" ) , Integer.toString( i ) );
      result.put( headerName , createHeaderPlugin( headerSetting ) );
    }

    return result;
  }

  public static IHeaderPlugin createHeaderPlugin( final Map headerSetting ) throws IOException {
    String headerClassName = YamlObjectUtils.getString( headerSetting.get( "class" ) , null );
    IHeaderPlugin headerPluging = FindHeaderPlugin.get( headerClassName );
    Map options = YamlObjectUtils.getMap( headerSetting.get( "options" ) );
    headerPluging.setup( options );
    return headerPluging;
  }

  public static Map<String,ILogger> createLoggerMapping( final Map setting ) throws IOException {
    List loggerSettingList = YamlObjectUtils.getList( setting.get( "loggers" ) );

    Map<String,ILogger> result = new HashMap<String,ILogger>();
    for ( int i = 0 ; i < loggerSettingList.size() ; i++ ) {
      Map loggerSetting = YamlObjectUtils.getMap( loggerSettingList.get( i ) );
      String loggerName = YamlObjectUtils.getString( loggerSetting.get( "name" ) , Integer.toString( i ) );
      result.put( loggerName , createLogger( loggerSetting ) );
    }
    return result;
  }

  public static ILogger createLogger( final Map loggerSetting ) throws IOException {
    String loggerClassName = YamlObjectUtils.getString( loggerSetting.get( "class" ) , DummyLogger.class.getName() );
    ILogger logger = FindLogger.get( loggerClassName );
    Map options = YamlObjectUtils.getMap( loggerSetting.get( "options" ) );
    logger.setup( options );
    return logger;
  }

  public static ServletAndPath[] createServles( final Map setting , final Map<String,IHeaderPlugin> headerPluginMapping , final Map<String,ILogger> loggerMapping ) throws IOException {
    List servletSettingList = YamlObjectUtils.getList( setting.get( "servlets" ) );
    ServletAndPath[] result = new ServletAndPath[servletSettingList.size()];
    for ( int i = 0 ; i < servletSettingList.size() ; i++ ) {
      Map servletSetting = YamlObjectUtils.getMap( servletSettingList.get( i ) );
      String loggerName = YamlObjectUtils.getString( servletSetting.get( "path" ) , "/" );
      AbstractLoggerServlet servlet = createServlet( servletSetting , headerPluginMapping , loggerMapping );
      result[i] = new ServletAndPath( servlet , loggerName );
    }
    return result;
  }

  public static AbstractLoggerServlet createServlet( final Map servletSetting , final Map<String,IHeaderPlugin> headerPluginMapping , final Map<String,ILogger> loggerMapping ) throws IOException {
    String servletClassName = YamlObjectUtils.getString( servletSetting.get( "class" ) , DummyLoggerServlet.class.getName() );
    AbstractLoggerServlet servlet = FindServlet.get( servletClassName );
    List loggersSettingList = YamlObjectUtils.getList( servletSetting.get( "loggers" ) );
    ILogger[] loggers = new ILogger[ loggersSettingList.size() ];
    for ( int i = 0 ; i < loggersSettingList.size() ; i++ ) {
      String loggerName = YamlObjectUtils.getString( loggersSettingList.get( i ) , null );
      if ( ! loggerMapping.containsKey( loggerName ) ) {
        throw new IOException( "Logger was not found: " + loggerName );
      }
      loggers[i] = loggerMapping.get( loggerName );
    }
    List headerSettingList = YamlObjectUtils.getList( servletSetting.get( "headers" ) );
    HeaderProcess headerProcess = new HeaderProcess();
    for ( int i = 0 ; i < headerSettingList.size() ; i++ ) {
      String headerName = YamlObjectUtils.getString( headerSettingList.get( i ) , null );
      if ( ! headerPluginMapping.containsKey( headerName ) ) {
        throw new IOException( "HeaderPlugin was not found: " + headerName );
      }
      headerProcess.put( headerName , headerPluginMapping.get( headerName ) );
    }
    Map options = YamlObjectUtils.getMap( servletSetting.get( "options" ) );
    servlet.setup( options );
    servlet.setLoggers( new Loggers( loggers ) );
    servlet.setHeaderProcess( headerProcess );
    return servlet;
  }

}
