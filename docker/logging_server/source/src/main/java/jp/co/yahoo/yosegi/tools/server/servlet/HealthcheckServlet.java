package jp.co.yahoo.yosegi.tools.logging.server.servlet;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthcheckServlet extends AbstractLoggerServlet {

  @Override
  public final void setup( final Map options ) {}

  @Override
  protected void doPost(
      HttpServletRequest request,
      HttpServletResponse response ) throws ServletException, IOException {
    getLoggers().logging( Long.valueOf( System.currentTimeMillis() ).toString().getBytes() );
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
  }

}
