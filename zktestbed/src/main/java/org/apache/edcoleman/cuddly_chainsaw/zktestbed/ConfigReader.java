package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.accumulo.core.data.TableId;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface ConfigReader {

  Optional<PropValue> getProperty(final TableId tableId, final String name);

  boolean haveProperty(final TableId tableId, final String name);

  boolean isTableProperty(final String name);
  boolean isSystemProperty(final String name);
  boolean isDefaultProperty(final String name);

  Map<String,PropValue> getProperties(final TableId tableId, Predicate<String> filter);
}
