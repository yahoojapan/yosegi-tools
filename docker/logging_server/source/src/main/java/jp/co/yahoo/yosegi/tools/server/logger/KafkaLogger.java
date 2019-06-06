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

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaLogger implements ILogger {

  private final Properties properties = new Properties();

  private KafkaProducer<String,Bytes> producer;
  private String topicName = "default_logger_topic";

  @Override
  public void setup( final Map setting ) throws IOException {
    if( setting.containsKey( "zookeeper_servers" ) ){
      properties.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG ,
          setting.get( "zookeeper_servers" ).toString() );
    }
    if( setting.containsKey( "batch_size" ) ){
      properties.put(
          ProducerConfig.BATCH_SIZE_CONFIG ,
          Integer.parseInt( setting.get( "batch_size" ).toString() ) );
    }
    if( setting.containsKey( "acks" ) ){
      properties.put(
          ProducerConfig.ACKS_CONFIG ,
          setting.get( "acks" ).toString() );
    }
    if( setting.containsKey( "compression_type" ) ){
      properties.put(
          ProducerConfig.COMPRESSION_TYPE_CONFIG ,
          setting.get( "compression_type" ).toString() );
    }
    if( setting.containsKey( "topic_name" ) ){
      topicName = setting.get( "topic_name" ).toString();
    }
    properties.put("timeout.ms", "3000");

    producer = new KafkaProducer<String,Bytes>(
        properties ,
        new StringSerializer(),
        new BytesSerializer() );
  }

  private void reopen() throws IOException {
    producer.close();
    producer = new KafkaProducer<String,Bytes>(
        properties ,
        new StringSerializer(),
        new BytesSerializer() );
  }

  @Override
  public boolean logging( final byte[] message ) throws IOException {
    try {
      producer.send( new ProducerRecord<String,Bytes>( topicName , new Bytes( message ) ) ).get();
    } catch ( InterruptedException e ) {
      return false;
    } catch ( ExecutionException e ) {
      reopen();
      return false;
    }
    return true;
  }

}
