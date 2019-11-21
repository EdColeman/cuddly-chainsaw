package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

public class PropName implements Comparable<PropName>{

  private final PropScope scope;
  private final String name;

  private PropName(final PropScope scope, final String name){
    this.scope = scope;
    this.name = name;
  }

  public static PropName systemProp(final String name){
    return new PropName(PropScope.SYSTEM,name);
  }

  public static PropName tableProp(final String name){
    return new PropName(PropScope.TABLE,name);
  }

  @Override public int compareTo(PropName other) {
    int r = this.scope.compareTo(other.scope);
    if(r != 0){
      return r;
    }
    return this.name.compareTo(other.name);
  }
}
