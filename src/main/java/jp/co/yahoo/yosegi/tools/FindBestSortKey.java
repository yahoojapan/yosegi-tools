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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import jp.co.yahoo.yosegi.config.Configuration;
import jp.co.yahoo.yosegi.message.formatter.json.JacksonMessageWriter;
import jp.co.yahoo.yosegi.message.formatter.IStreamWriter;
import jp.co.yahoo.yosegi.message.formatter.IMessageWriter;
import jp.co.yahoo.yosegi.message.parser.IStreamReader;
import jp.co.yahoo.yosegi.message.parser.IParser;
import jp.co.yahoo.yosegi.message.parser.json.JacksonMessageReader;
import jp.co.yahoo.yosegi.reader.YosegiSchemaReader;
import jp.co.yahoo.yosegi.reader.YosegiStatsReader;
import jp.co.yahoo.yosegi.stats.ColumnStats;
import jp.co.yahoo.yosegi.util.ByteLineReader;
import jp.co.yahoo.yosegi.writer.YosegiSchemaFileWriter;

public class FindBestSortKey {


  private FindBestSortKey() {}

  public static class KeyAndRealDataSize implements Comparable<KeyAndRealDataSize>,Comparator<KeyAndRealDataSize> {

    public final List<String> keys;
    public final Long realDataSize;

    public KeyAndRealDataSize( final List<String> keys , final Long realDataSize ) {
      this.keys = keys;
      this.realDataSize = realDataSize;
    }

    @Override
    public int compare( final KeyAndRealDataSize k1 , final KeyAndRealDataSize k2 ) {
      return Long.compare( k1.realDataSize , k2.realDataSize );
    }

    @Override
    public int compareTo( final KeyAndRealDataSize target ) {
      return realDataSize.compareTo( target.realDataSize );
    }

    @Override
    public String toString() {
      Map<Object,Object> dataSet = new HashMap<Object,Object>();
      dataSet.put( "keys" , keys );
      dataSet.put( "real_data_size" , realDataSize );
      try {
        return new String( new JacksonMessageWriter().create( dataSet ) );
      } catch ( IOException ex ) {
        throw new RuntimeException( ex );
      }
    }

  }

  public static Options createOptions( final String[] args ){

    Option input = OptionBuilder.
      withLongOpt("input").
      withDescription("Input file path.").
      hasArg().
      isRequired().
      withArgName("input").
      create( 'i' );

    Option output = OptionBuilder.
      withLongOpt("output").
      withDescription("output and work directory path.").
      hasArg().
      isRequired().
      withArgName("output").
      create( 'o' );

    Option lines = OptionBuilder.
      withLongOpt("lines").
      withDescription("Number of input lines.").
      hasArg().
      withArgName("lines").
      create( 'n' );

    Option columns = OptionBuilder.
      withLongOpt("columns").
      withDescription("Number of colums.").
      hasArg().
      withArgName("columns").
      create( 'c' );

    Option help = OptionBuilder.
      withLongOpt("help").
      withDescription("help").
      withArgName("help").
      create( 'h' );

    Options  options = new Options();

    return options
      .addOption( input )
      .addOption( output )
      .addOption( lines )
      .addOption( columns )
      .addOption( help );
  }

  public static void printHelp( final String[] args ){
    HelpFormatter hf = new HelpFormatter();
    hf.printHelp( "[options]" , createOptions( args ) );
  }

  public static void createOriginalFile(
      final String input , final String originalFile , final long lines ) throws IOException {
    Configuration config = new Configuration();

    InputStream in = FileUtil.fopen( input );
    YosegiSchemaReader reader = new YosegiSchemaReader();
    File file = new File( input );
    long fileLength = file.length();
    reader.setNewStream( in , fileLength , config );

    OutputStream out = FileUtil.create( originalFile );
    IStreamWriter writer = StreamWriterFactory.create( out , "json" , null );

    long inCount = 0;
    long skipCount = 0;
    while( reader.hasNext() && inCount < lines ){
      if( skipCount < 10000 ) {
        skipCount++;
        continue;
      }
      writer.write( reader.next() );
      inCount++;
    }
    writer.close();
    reader.close();
  }

  public static List<KeyAndRealDataSize> getColumnStats( final String input ) throws IOException {
    Configuration config = new Configuration();

    InputStream in = FileUtil.fopen( input );
    YosegiStatsReader reader = new YosegiStatsReader();
    File file = new File( input );
    long fileLength = file.length();
    reader.readStream( in , fileLength , config );
    ColumnStats stats = reader.getTotalColumn();
    List<KeyAndRealDataSize> list = new ArrayList<KeyAndRealDataSize>();
    List<String> currentKeyList = new ArrayList<String>();
    addChildColumnStats( currentKeyList , list , stats.getChildColumnStats() );
    Collections.sort( list , Collections.reverseOrder() );
    return list;
  }

  public static void addChildColumnStats(
      final List<String> parentKeyList,
      final List<KeyAndRealDataSize> list ,
      final Map<String,ColumnStats> childColumns ) {
    for ( Map.Entry<String,ColumnStats> entry : childColumns.entrySet() ) {
      if ( entry.getKey().equals( "ARRAY" ) ) {
        continue;
      }
      List<String> currentKeys = new ArrayList<String>( parentKeyList );
      currentKeys.add( entry.getKey() );
      long dataSize = entry.getValue().getTotalStats().getRealDataSize();
      list.add( new KeyAndRealDataSize( currentKeys , Long.valueOf( dataSize ) ) );
      addChildColumnStats( currentKeys , list , entry.getValue().getChildColumnStats() );
    }
  }

  public static void createColumsFile(
      final String outputPath , final List<KeyAndRealDataSize> list ) throws IOException {
    File file = new File( outputPath );
    BufferedWriter bw = new BufferedWriter( new FileWriter( file ) );
    for( KeyAndRealDataSize k : list ) {
      bw.write( k.toString() );
      bw.newLine();
    }
    bw.close();
  }

  public static KeyAndRealDataSize runCreateFile(
      final String target , final String tmpPath , final KeyAndRealDataSize k ) throws IOException {
    Process process = new ProcessBuilder ( "/bin/sort" , "-S128m" ).start();
    OutputStream sortOut = process.getOutputStream();
    InputStream in = FileUtil.fopen( target );
    IStreamReader reader = StreamReaderFactory.create( in , "json" , null );
    JacksonMessageWriter jsonWriter = new JacksonMessageWriter();
    while ( reader.hasNext() ) {
      IParser parser = reader.next();
      IParser currentParser = parser;
      for( int i = 0 ; i < k.keys.size() - 1 ; i++ ) {
        currentParser = currentParser.getParser( k.keys.get( i ) );
      }
      byte[] sortKey = null;
      String sortKeyStr = currentParser.get( k.keys.get( k.keys.size() - 1 ) ).getString();
      if ( sortKeyStr != null ) {
        sortKey = sortKeyStr.replace( "" , "" ).getBytes();
      }
      byte[] json = jsonWriter.create( parser );
      if ( sortKey != null ) {
        sortOut.write( sortKey );
      }
      sortOut.write( (byte)'');
      sortOut.write( json );
      sortOut.write( (byte)'\n');
    }
    sortOut.close();

    File yosegiFile = new File( tmpPath );
    YosegiSchemaFileWriter writer = new YosegiSchemaFileWriter( yosegiFile , new Configuration() );
    JacksonMessageReader jsonReader = new JacksonMessageReader();
    ByteLineReader lineReader = new ByteLineReader( process.getInputStream() );
    int lineLength = lineReader.readLine();
    while ( 0 <= lineLength ) {
      byte[] buffer = lineReader.get();
      int start = 0;
      for ( ; start < lineLength ; start++ ) {
        if ( buffer[start] == (byte)'' ) {
          start++;
          break;
        }
      }
      writer.write( jsonReader.create( buffer , start , ( lineLength - start ) ) );
      lineLength = lineReader.readLine();
    }
    lineReader.close();
    writer.close();

    long fileSize = yosegiFile.length();
    yosegiFile.delete();

    return new KeyAndRealDataSize( k.keys , fileSize );
  }

  public static int run( final String[] args ) throws IOException{
    CommandLine cl;
    try{
      CommandLineParser clParser = new GnuParser();
      cl = clParser.parse( createOptions( args ) , args );
    }catch( ParseException e ){
      printHelp( args );
      throw new IOException( e );
    }

    if( cl.hasOption( "help" ) ){
      printHelp( args );
      return 0;
    }

    String input = cl.getOptionValue( "input" , null );
    String output = cl.getOptionValue( "output" , null );
    long lines = Long.parseLong( cl.getOptionValue( "lines" , "500000" ) );
    int columns = Integer.parseInt( cl.getOptionValue( "columns" , "10" ) );

    File workDir = new File( output );
    if ( workDir.exists() ) {
      throw new IOException( "Output directory is already exists." + workDir );
    }
    if ( ! workDir.mkdirs() ) {
      throw new IOException( "Could not create output directory." + workDir );
    }

    String originalFile = String.format( "%s/original.json" , output );
    createOriginalFile( input , originalFile , lines );

    List<KeyAndRealDataSize> columnList = getColumnStats( input );
    String columnsFile = String.format( "%s/columns.json" , output );
    createColumsFile( columnsFile , columnList );

    List<KeyAndRealDataSize> resultList = new ArrayList<KeyAndRealDataSize>();
    int columnCount = 0;
    String tmpFilePath = String.format( "%s/tmp.yosegi" , output );
    for( KeyAndRealDataSize k : columnList ) {
      resultList.add( runCreateFile( originalFile , tmpFilePath , k ) );
      columnCount++;
      if( columnCount == columns ){
        break;
      }
    }
    Collections.sort( resultList );
    String resultFile = String.format( "%s/result.json" , output );
    createColumsFile( columnsFile , resultList );
    for ( KeyAndRealDataSize k : resultList ) {
      System.out.println( k.toString() );
    }

    return 0;
  }

  public static void main( final String[] args ) throws IOException{
    System.exit( run( args ) );
  }

}
