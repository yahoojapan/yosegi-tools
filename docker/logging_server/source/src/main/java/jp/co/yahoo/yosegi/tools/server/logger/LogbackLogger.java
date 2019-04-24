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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.util.FileSize;

import jp.co.yahoo.yosegi.config.Configuration;
import jp.co.yahoo.yosegi.tools.StreamReaderFactory;
import jp.co.yahoo.yosegi.tools.logging.server.config.YamlObjectUtils;
import jp.co.yahoo.yosegi.writer.YosegiRecordWriter;
import jp.co.yahoo.yosegi.message.parser.IStreamReader;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class LogbackLogger implements ILogger {

  private RollingFileAppender<byte[]> fileAppender;

  public class YosegiTimeBasedRollingPolicy extends TimeBasedRollingPolicy {

    private final TimeBasedFileNamingAndTriggeringPolicy<byte[]> timeBasedFileNamingAndTriggeringPolicy;

    public YosegiTimeBasedRollingPolicy() {
      timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<byte[]>();
      setTimeBasedFileNamingAndTriggeringPolicy( timeBasedFileNamingAndTriggeringPolicy );
    }

    @Override
    public void rollover() throws RolloverFailure {
      String elapsedPeriodsFileName = timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();

      String parentsRawFile = getParentsRawFileProperty();
      String tmpTarget = parentsRawFile + System.nanoTime() + ".tmp";
      new File( parentsRawFile ).renameTo( new File( tmpTarget ) );
      ExecutorService executorService = context.getScheduledExecutorService();
      executorService.submit( new CompressionRunnable( tmpTarget , elapsedPeriodsFileName ) );

      Date now = new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
      timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover().cleanAsynchronously(now);
    }

  }

  private class CompressionRunnable implements Runnable {

    private final String inputFile;
    private final String outputFile;

    public CompressionRunnable( final String inputFile , final String outputFile ) {
      this.inputFile = inputFile;
      this.outputFile = outputFile;
    }

    @Override
    public void run() {
      try {
        InputStream in = new FileInputStream( inputFile );
        IStreamReader reader = StreamReaderFactory.create( in , "json" , null );
        String tmpFileName = "." + outputFile;
        OutputStream out = new FileOutputStream( tmpFileName );

        Configuration config = new Configuration();
        YosegiRecordWriter writer = new YosegiRecordWriter( out , config );

        while( reader.hasNext() ){
          writer.addParserRow( reader.next() );
        }
        writer.close();
        new File( tmpFileName ).renameTo( new File( outputFile ) );
        new File( inputFile ).delete();
      } catch ( IOException ex ) {
        throw new RuntimeException( ex );
      }
    }

  }

  private class ByteArrayEncoder extends EncoderBase<byte[]> {

    @Override
    public byte[] encode( final byte[] event ) {
      byte[] result = new byte[event.length + 1];
      System.arraycopy( event , 0 , result , 0 , event.length );
      result[result.length-1] = '\n';
      return result;
    }

    @Override
    public byte[] headerBytes() {
      return null;
    }

    @Override
    public byte[] footerBytes() {
      return null;
    }

  }

  @Override
  public void setup( final Map setting ) throws IOException {
    int maxHistory = YamlObjectUtils.getInt( setting.get( "max_history" ) , 60 );
    long sizeCap = YamlObjectUtils.getLong( setting.get( "size_cap" ) , FileSize.GB_COEFFICIENT );
    String rotateFileNamePattern = YamlObjectUtils.getString( setting.get( "rotate_file_pattern" ) , "/tmp/archive.%d{yyyy-MM-dd-HH-mm}.yosegi" );
    String currentLogFile = YamlObjectUtils.getString( setting.get( "current_log_file" ) , "/tmp/current.log" );

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    ByteArrayEncoder encoder = new ByteArrayEncoder();
    encoder.setContext(lc);
    encoder.start();

    fileAppender = new RollingFileAppender<>();
    YosegiTimeBasedRollingPolicy rollingPolicy = new YosegiTimeBasedRollingPolicy();
    rollingPolicy.setFileNamePattern( rotateFileNamePattern );
    rollingPolicy.setMaxHistory( maxHistory );
    rollingPolicy.setTotalSizeCap( new FileSize( sizeCap ) );
    rollingPolicy.setParent(fileAppender);
    rollingPolicy.setContext(lc);
    rollingPolicy.start();

    fileAppender.setFile( currentLogFile );
    fileAppender.setAppend( false );
    fileAppender.setEncoder( encoder );
    fileAppender.setRollingPolicy( rollingPolicy );
    fileAppender.setContext( lc );
    fileAppender.start();
  }

  @Override
  public boolean logging( final byte[] message ) throws IOException {
    fileAppender.doAppend( message );
    return true;
  }

}
