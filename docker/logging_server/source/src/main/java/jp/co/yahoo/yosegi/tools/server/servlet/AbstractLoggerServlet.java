package jp.co.yahoo.yosegi.tools.logging.server.servlet;

import jp.co.yahoo.yosegi.tools.logging.server.header.HeaderProcess;
import jp.co.yahoo.yosegi.tools.logging.server.logger.Loggers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

public abstract class AbstractLoggerServlet extends HttpServlet {

  private Loggers loggers;
  private HeaderProcess headerProcess;

  public final void setLoggers( final Loggers loggers ) {
    this.loggers = loggers;
  }

  public final Loggers getLoggers() {
    return loggers;
  }

  public final void setHeaderProcess( final HeaderProcess headerProcess ) {
    this.headerProcess = headerProcess;
  }

  public final HeaderProcess getHeaderProcess() {
    return headerProcess;
  }

  public abstract void setup( final Map options );

}
