package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.accumulo.core.data.TableId;

import java.util.Map;

public interface ConfigWriter {

  boolean setGlobalProperty(final String name, final PropValue value);
  boolean updateGlobalProperty(final String name, final PropValue current, final PropValue update);

  boolean setProperty(final TableId tableId, final String name, final PropValue value);
  boolean updateProperty(final TableId tableId, final String name, final PropValue current, final PropValue update);

  boolean setGlobalProperties(final Map<String, PropValue> propMap);
  boolean setProperties(final TableId tableId, final Map<String, PropValue> propMap);
}
