package it.gov.pagopa.apiconfig.cache.model;

import org.springframework.core.convert.converter.Converter;

public class StringToNodeCacheKeyConverter implements Converter<String, NodeCacheKey> {
  @Override
  public NodeCacheKey convert(String source) {
    String uppercased = source.replaceAll("([A-Z])","_$1").toUpperCase();
    return NodeCacheKey.valueOf(uppercased);
  }
}