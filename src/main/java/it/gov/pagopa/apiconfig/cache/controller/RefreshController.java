package it.gov.pagopa.apiconfig.cache.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.ProblemJson;
import it.gov.pagopa.apiconfig.cache.model.RefreshResponse;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    @Value("${xls.mask-passwords}")
    private boolean xlsMaskPasswords;


    @PostConstruct
    public void preloadKeysFromRedis() {
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
            summary = "Get in memory cache,full or only the supplied keys",
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
    public ResponseEntity<Map<String,Object>> get(@RequestParam(required = false) List<String> keys)
            throws IOException {

        if(inMemoryCache == null){
            throw new AppException(AppError.CACHE_NOT_INITIALIZED);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-CACHE-ID",(String)inMemoryCache.get(Constants.version));
        responseHeaders.set("X-CACHE-TIMESTAMP", DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)inMemoryCache.get(Constants.timestamp)));
        responseHeaders.set("X-CACHE-VERSION",(String)inMemoryCache.get(Constants.cacheVersion));
        if(keys!=null && !keys.isEmpty()){
            Map<String,Object> returnMap = new HashMap<>();
            keys.forEach(k->{
                if(inMemoryCache.containsKey(k)){
                    returnMap.put(k,inMemoryCache.get(k));
                }
            });
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(returnMap);
        }else{
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(inMemoryCache);
        }
    }

    @Operation(
            summary = "Get the list of available cache keys",
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
    @GetMapping(value="/keys",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<String> getKeys() {
        return Arrays.asList(
                        Constants.version,
                        Constants.creditorInstitutions,
                        Constants.creditorInstitutionBrokers,
                        Constants.stations,
                        Constants.creditorInstitutionStations,
                        Constants.encodings,
                        Constants.creditorInstitutionEncodings,
                        Constants.ibans,
                        Constants.creditorInstitutionInformations,
                        Constants.psps,
                        Constants.pspBrokers,
                        Constants.paymentTypes,
                        Constants.pspChannelPaymentTypes,
                        Constants.plugins,
                        Constants.pspInformationTemplates,
                        Constants.pspInformations,
                        Constants.channels,
                        Constants.cdsServices,
                        Constants.cdsSubjects,
                        Constants.cdsSubjectServices,
                        Constants.cdsCategories,
                        Constants.configurations,
                        Constants.ftpServers,
                        Constants.languages,
                        Constants.gdeConfigurations,
                        Constants.metadataDict
        );
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
    public ResponseEntity<CacheVersion> id()
            throws IOException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-CACHE-ID",(String)inMemoryCache.get(Constants.version));
        responseHeaders.set("X-CACHE-TIMESTAMP", DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)inMemoryCache.get(Constants.timestamp)));
        responseHeaders.set("X-CACHE-VERSION",(String)inMemoryCache.get(Constants.cacheVersion));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(new CacheVersion((String)inMemoryCache.get(Constants.version)));
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
        boolean cacheV1InProgress = configService.getCacheV1InProgress(Constants.full);
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
            configService.sendEvent(
                    (String)inMemoryCache.get(Constants.version),
                    (ZonedDateTime)inMemoryCache.get(Constants.timestamp)
            );
    }


    @Operation(
            summary = "Get xlsx of full chache",
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
                                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
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
    @GetMapping(value = "/xlsx",
            produces = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public ResponseEntity<byte[]> xls()
            throws IOException {
        byte[] convert = null;
        try {
            convert = new JsonToXls(xlsMaskPasswords).convert(inMemoryCache);
        } catch (Exception e){
            log.error("Error creating xlsx",e);
        }

        return ResponseEntity.ok()
                .body(convert);
    }

}
