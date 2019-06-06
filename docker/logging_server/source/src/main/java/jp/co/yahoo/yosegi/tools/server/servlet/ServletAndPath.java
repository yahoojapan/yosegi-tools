package jp.co.yahoo.yosegi.tools.logging.server.servlet;

public class ServletAndPath {

  private final AbstractLoggerServlet servlet;
  private final String path;

  public ServletAndPath( final AbstractLoggerServlet servlet , final String path ) {
    this.servlet = servlet;
    this.path = path;
  }

  public AbstractLoggerServlet getServlet() {
    return servlet;
  }

  public String getPath() {
    return path;
  }

}
