package org.apache.edcoleman.cuddly_chainsaw.mmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

public class SerTest {

  @Test public void simple() throws Exception {

    String key = "a-key";
    Multimap<String, ValueClass> map = ArrayListMultimap.create();

    map.put(key, new ValueClass("name1", "prop1"));
    map.put(key, new ValueClass("name2", "prop2"));

    ObjectMapper mapper = new ObjectMapper()
        ; //.registerModule(new GuavaModule());

    String jsonResult = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(map.asMap());

    System.out.println(jsonResult);
  }

  private static class ValueClass {

    private String aName;
    private String aProp;

    public ValueClass(final String aName, final String aProp){
      this.aName = aName;
      this.aProp = aProp;
    }

    public String getaName() {
      return aName;
    }

    public void setaName(String aName) {
      this.aName = aName;
    }

    public String getaProp() {
      return aProp;
    }

    public void setaProp(String aProp) {
      this.aProp = aProp;
    }

    @Override public String toString() {
      return new StringJoiner(", ", ValueClass.class.getSimpleName() + "[", "]")
          .add("aName='" + aName + "'").add("aProp='" + aProp + "'").toString();
    }
  }
}
