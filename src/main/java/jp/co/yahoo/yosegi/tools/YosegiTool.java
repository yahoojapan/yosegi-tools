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
package jp.co.yahoo.yosegi.tools;

import java.io.IOException;

public final class YosegiTool{

  private YosegiTool(){}

  public static void printHelp(){
    System.err.println( "create create file." );
    System.err.println( "cat read yosegi file." );
    System.err.println( "schema view yosegi file schema." );
    System.err.println( "fstats view yosegi file stats." );
    System.err.println( "cstats view column stats." );
    System.err.println( "stest run storage perfomance test." );
    System.err.println( "merge Merge Yosegi files." );
    System.err.println( "to_arrow Create Arrow binary." );
    System.err.println( "from_arrow Create Yosegi file from Arrow binary." );
    System.err.println( "help view help." );
  }

  public static void main( final String[] args ) throws IOException{
    if( args.length == 0 ){
      printHelp();
      System.exit( 255 );
    }

    String command = args[0];
    String[] commandArgs = new String[ args.length - 1 ];
    System.arraycopy( args , 1 , commandArgs , 0 , commandArgs.length );
    if( "create".equals( command ) ){
      WriterTool.main( commandArgs );
    }
    else if( "cat".equals( command ) ){
      ReaderTool.main( commandArgs );
    }
    else if( "schema".equals( command ) ){
      SchemaTool.main( commandArgs );
    }
    else if( "fstats".equals( command ) ){
      StatsTool.main( commandArgs );
    }
    else if( "cstats".equals( command ) ){
      ColumnStatsTool.main( commandArgs );
    }
    else if( "stest".equals( command ) ){
      StoragePerformanceTool.main( commandArgs );
    }
    else if( "merge".equals( command ) ){
      MergeTool.main( commandArgs );
    }
    else if( "to_arrow".equals( command ) ){
      ConvertArrowFormatTool.main( commandArgs );
    }
    else if( "from_arrow".equals( command ) ){
      ConvertArrowFormatToYosegi.main( commandArgs );
    }
    else if( "help".equals( command ) ){
      printHelp();
      System.exit( 0 );
    }
    else{
      System.err.println( String.format( "Unknown command %s" , command ) );
      printHelp();
      System.exit( 255 );
    }
  }

}
