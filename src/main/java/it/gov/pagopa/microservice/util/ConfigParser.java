package it.gov.pagopa.microservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.microservice.model.ConfigData;

public class ConfigParser {
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    public ConfigData readConfig(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ConfigData.class);
    }
    public String writeConfig(ConfigData data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}
