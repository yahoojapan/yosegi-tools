package jp.co.yahoo.yosegi.tools.logging.server.logger;

import jp.co.yahoo.yosegi.tools.logging.server.config.YamlObjectUtils;

import org.fluentd.logger.FluentLogger;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class FluentdLogger implements ILogger {

  private FluentLogger logger;
  private String label;
  private String messageKey;

  @Override
  public void setup( final Map setting ) throws IOException {
    String tag = YamlObjectUtils.getString( setting.get( "tag" ) , "default" );
    String host = YamlObjectUtils.getString( setting.get( "host" ) , "localhost" );
    int port = YamlObjectUtils.getInt( setting.get( "port" ) , 24224 );
    logger = FluentLogger.getLogger( tag , host , port );
    
    label = YamlObjectUtils.getString( setting.get( "label" ) , "default" );
    messageKey = YamlObjectUtils.getString( setting.get( "message_key" ) , "message" );
  }

  @Override
  public boolean logging( final byte[] message ) throws IOException {
    return logger.log( label , messageKey , message );
  }

}
