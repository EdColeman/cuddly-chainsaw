package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import java.util.Map;

public interface ConfigWriter {

  boolean setProperty(final String name, final PropValue value);
  boolean updateProperty(final String name, final PropValue current, final PropValue update);

  boolean setProperties(final Map<String, PropValue> propMap);
}
