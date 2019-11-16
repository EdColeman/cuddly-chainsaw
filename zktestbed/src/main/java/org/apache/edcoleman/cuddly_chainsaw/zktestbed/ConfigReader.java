package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface ConfigReader {

  Optional<PropValue> getProperty(final String name);

  boolean haveProperty(final String name);

  boolean isTableProperty(final String name);
  boolean isSystemProperty(final String name);
  boolean isDefaultProperty(final String name);

  Map<String,PropValue> getProperties(Predicate<String> filter);
}
