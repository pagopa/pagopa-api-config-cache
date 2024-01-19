package it.gov.pagopa.apiconfig.cache.controller;

import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/cache/schemas")
@Validated
@Slf4j
public class RefreshController {

    private Map<String, Object> inMemoryCache;

    @Autowired
    private ConfigService configService;

    @PostConstruct
    private void preloadKeysFromRedis() {
        try {
            inMemoryCache = configService.loadSingleKeysFromRedis(null);
        } catch (Exception e){
            log.warn("could not load single keys cache from redis");
        }
    }

    public Map<String,Object> getInMemoryCache() throws IOException {
        if(inMemoryCache==null){
            docache();
        }
        return inMemoryCache;
    }

    @GetMapping(
            value = "/v1/refresh",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity cache()
            throws IOException {
        boolean cacheV1InProgress = configService.getCacheV1InProgress(Constants.FULL);
        if (cacheV1InProgress) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }else{
            docache();
            return ResponseEntity.ok().build();
        }
    }

    private void docache() throws IOException {
            inMemoryCache = configService.newCacheV1();
    }

}
