package jp.co.yahoo.yosegi.tools.logging.server.header;

import java.util.HashMap;
import java.util.Map;

public class HeaderResult {

  private final Map<String,Object> result = new HashMap<String,Object>();

  public void put( final String name , final Object obj ) {
    result.put( name , obj );
  }

  public Object get( final String name ) {
    return result.get( name );
  }

  public int size() {
    return result.size();
  }

  public boolean containsKey( final String name ) {
    return result.containsKey( name );
  }

  public void setAllValue( final Map map ) {
    map.putAll( result );
  }

  @Override
  public String toString() {
    return result.toString();
  }

}
