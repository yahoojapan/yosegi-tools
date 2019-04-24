package jp.co.yahoo.yosegi.tools.logging.server.servlet;

import jp.co.yahoo.yosegi.tools.logging.server.header.HeaderProcess;
import jp.co.yahoo.yosegi.tools.logging.server.header.HeaderResult;
import jp.co.yahoo.yosegi.message.formatter.json.JacksonMessageWriter;
import jp.co.yahoo.yosegi.message.parser.json.JacksonMessageReader;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleJsonLoggerServlet extends AbstractLoggerServlet {

  private final JacksonMessageReader jsonReader = new JacksonMessageReader();
  private final JacksonMessageWriter jsonWriter = new JacksonMessageWriter();

  @Override
  public final void setup( final Map options ) {}

  @Override
  protected void doPost(
      HttpServletRequest req,
      HttpServletResponse res ) throws ServletException, IOException {
    res.setContentType("text/html");
    String json = req.getParameter( "message" );
    if ( json == null ) {
      res.setStatus( HttpServletResponse.SC_BAD_REQUEST );
      return;
    }
    HeaderProcess headerProcess = getHeaderProcess();
    HeaderResult headerResult = headerProcess.get( req );

    byte[] sendMessage;
    try { 
      Map jsonJavaObj = (Map)( jsonReader.create( json ).toJavaObject() );
      headerResult.setAllValue( jsonJavaObj );
      sendMessage = jsonWriter.create( jsonJavaObj );
    } catch ( IOException e ) {
      res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      return;
    }
    boolean[] sendResults = getLoggers().logging( sendMessage );
    for ( boolean sendResult : sendResults ){
      if ( ! sendResult ) {
        res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        return;
      }
    }
    res.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  protected void doGet(
      HttpServletRequest req,
      HttpServletResponse res ) throws ServletException, IOException {
  }

}
