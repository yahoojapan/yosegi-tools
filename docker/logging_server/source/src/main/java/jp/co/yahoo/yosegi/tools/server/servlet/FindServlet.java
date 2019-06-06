package jp.co.yahoo.yosegi.tools.logging.server.servlet;

import jp.co.yahoo.yosegi.util.FindClass;

import java.io.IOException;

public final class FindServlet {

  private FindServlet() {}

  public static AbstractLoggerServlet get( final String className ) throws IOException {
    Object obj = FindClass.getObject( className , true , FindServlet.class.getClassLoader() );
    if( ! ( obj instanceof AbstractLoggerServlet ) ){
      throw new IOException( String.format( "Invalid LoggerServlet class: " + className ) );
    }
    return (AbstractLoggerServlet)obj;
  }

}
