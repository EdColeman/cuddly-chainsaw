package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import java.util.Objects;
import java.util.StringJoiner;

public class PropValue {

  private final String value;

  public PropValue(final String v){
    value = v;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PropValue propValue = (PropValue) o;
    return Objects.equals(value, propValue.value);
  }

  @Override public int hashCode() {
    return Objects.hash(value);
  }

  @Override public String toString() {
    return new StringJoiner(", ", PropValue.class.getSimpleName() + "[", "]")
        .add("value='" + value + "'").toString();
  }
}
