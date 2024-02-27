package it.gov.pagopa.apiconfig.cache.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.apiconfig.cache.model.ProblemJson;
import it.gov.pagopa.apiconfig.cache.model.RefreshResponse;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/cache")
@Validated
@Slf4j
public class RefreshController {

    private Map<String, Object> inMemoryCache;

    @Autowired
    private ConfigService configService;

    @Value("${preload:true}")
    private Boolean preload;


    @PostConstruct
    private void preloadKeysFromRedis() {
        if(preload){
            try {
                inMemoryCache = configService.loadFullCache();
            } catch (Exception e){
                log.warn("could not load single keys cache from redis");
            }
        }
    }

    public Map<String,Object> getInMemoryCache() throws IOException {
        if(inMemoryCache==null){
            docache();
        }
        return inMemoryCache;
    }

    @Operation(
            summary = "Get in memory full cache",
            security = {@SecurityRequirement(name = "ApiKey")},
            tags = {
                    "Cache",
            })
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Too many requests",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Service unavailable",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class)))
            })
    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String,Object>> get()
            throws IOException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-CACHE-ID",(String)inMemoryCache.get(Constants.version));
        responseHeaders.set("X-CACHE-TIMESTAMP", DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)inMemoryCache.get(Constants.timestamp)));
        responseHeaders.set("X-CACHE-VERSION",(String)inMemoryCache.get(Constants.cacheVersion));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(inMemoryCache);
    }

    @Operation(
            summary = "Get in memory full cache id",
            security = {@SecurityRequirement(name = "ApiKey")},
            tags = {
                    "Cache",
            })
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CacheVersion.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Too many requests",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Service unavailable",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class)))
            })
    @GetMapping(
            value = "/id",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity id()
            throws IOException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-CACHE-ID",(String)inMemoryCache.get(Constants.version));
        responseHeaders.set("X-CACHE-TIMESTAMP", DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)inMemoryCache.get(Constants.timestamp)));
        responseHeaders.set("X-CACHE-VERSION",(String)inMemoryCache.get(Constants.cacheVersion));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body((String)inMemoryCache.get(Constants.version));
    }

    @Operation(
            summary = "Refresh in memory full chache",
            security = {@SecurityRequirement(name = "ApiKey")},
            tags = {
                    "Cache",
            })
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Too many requests",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Service unavailable",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class)))
            })
    @GetMapping(
            value = "/refresh",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity refresh()
            throws IOException {
        boolean cacheV1InProgress = configService.getCacheV1InProgress(Constants.FULL);
        if (cacheV1InProgress) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }else{
            docache();

            String cacheVersion = (String)inMemoryCache.get(Constants.cacheVersion);
            String cacheId = (String)inMemoryCache.get(Constants.version);
            ZonedDateTime timestamp = (ZonedDateTime)inMemoryCache.get(Constants.timestamp);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("X-CACHE-ID",cacheId);
            responseHeaders.set("X-CACHE-TIMESTAMP",DateTimeFormatter.ISO_DATE_TIME.format(timestamp));
            responseHeaders.set("X-CACHE-VERSION",cacheVersion);

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(RefreshResponse.builder().version(cacheVersion).timestamp(timestamp).id(cacheId));
        }
    }

    private void docache() throws IOException {
            inMemoryCache = configService.newCacheV1();
    }

}
