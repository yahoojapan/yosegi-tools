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

import jp.co.yahoo.yosegi.tools.logging.server.header.IHeaderPlugin;
import jp.co.yahoo.yosegi.tools.logging.server.logger.ILogger;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.AbstractLoggerServlet;
import jp.co.yahoo.yosegi.tools.logging.server.servlet.ServletAndPath;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;

public class LoggerServer extends HttpServlet {

  public static void main( final String args[] ) throws Exception {
    Map setting;
    if ( args.length != 0 ) {
      Yaml yaml = new Yaml();
      setting = yaml.load( new FileInputStream( new File( args[0] ) ) );
    }
    else {
      setting = new HashMap<Object,Object>();
    }
    Server server = Setting.createServer( setting );
    Map<String,ILogger> loggerMapping = Setting.createLoggerMapping( setting );
    Map<String,IHeaderPlugin> headerPluginMapping = Setting.createHeaderPluginMapping( setting );
    ServletAndPath[] servlets = Setting.createServles( setting , headerPluginMapping , loggerMapping );
    
    setting.clear();
    ServletHandler handler = new ServletHandler();
    for ( ServletAndPath servlet : servlets ) {
      ServletHolder endpointServletHolder = new ServletHolder( servlet.getServlet() );
      handler.addServletWithMapping( endpointServletHolder , servlet.getPath() );
    }

    server.setHandler( handler );
    server.start();
    server.join();
  }

}
