package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.model.FullData;
import it.gov.pagopa.apiconfig.cache.model.FullDataList;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiConfigCacheResolver {

  @Autowired private CacheController cacheController;

  @GraphQLQuery(name = "getCache")
  public FullDataList getCache() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    HashMap<String, Object> cache = cacheController.getInMemoryCache();
    String jsonCache = objectMapper.writeValueAsString(cache);
    FullData fullData = objectMapper.readValue(jsonCache, FullData.class);

    FullDataList fullDataList = new FullDataList();
    fullDataList.setCacheVersion(fullData.getCacheVersion());
    fullDataList.setVersion(fullData.getVersion());
    fullDataList.setTimestamp(fullData.getTimestamp());
    fullDataList.setCreditorInstitutions(
        fullData.getCreditorInstitutions().values().stream().toList());
    return fullDataList;
  }

  @GraphQLQuery(name = "getFull")
  public HashMap<String, Object> getFull() throws IOException {
    return cacheController.getInMemoryCache();
  }

}
