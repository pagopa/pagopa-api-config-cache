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
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/cache")
@Validated
@Slf4j
public class CacheController {

  private final CacheConfigService cacheConfigService;
  private final boolean xlsMaskPasswords;

  public CacheController(
      CacheConfigService cacheConfigService,
      @Value("${xls.mask-passwords}") boolean xlsMaskPasswords) {
    this.cacheConfigService = cacheConfigService;
    this.xlsMaskPasswords = xlsMaskPasswords;
  }

  @Operation(
      summary = "Get the list of available cache keys",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {"Cache"})
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
  @GetMapping(
      value = "/keys",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<String> getKeys() {
    return Arrays.asList(
        Constants.VERSION,
        Constants.CREDITOR_INSTITUTIONS,
        Constants.CREDITOR_INSTITUTION_BROKERS,
        Constants.STATIONS,
        Constants.CREDITOR_INSTITUTION_STATIONS,
        Constants.MAINTENANCE_STATIONS,
        Constants.ENCODINGS,
        Constants.CREDITOR_INSTITUTION_ENCODINGS,
        Constants.IBANS,
        Constants.CREDITOR_INSTITUTION_INFORMATIONS,
        Constants.PSPS,
        Constants.PSP_BROKERS,
        Constants.PAYMENT_TYPES,
        Constants.PSP_CHANNEL_PAYMENT_TYPES,
        Constants.PLUGINS,
        Constants.PSP_INFORMATION_TEMPLATES,
        Constants.PSP_INFORMATIONS,
        Constants.CHANNELS,
        Constants.CDS_SERVICES,
        Constants.CDS_SUBJECTS,
        Constants.CDS_SUBJECT_SERVICES,
        Constants.CDS_CATEGORIES,
        Constants.CONFIGURATIONS,
        Constants.FTP_SERVERS,
        Constants.LANGUAGES,
        Constants.GDE_CONFIGURATIONS,
        Constants.METADATA_DICT);
  }

  @Operation(
      summary = "Get in memory cache, full or only the supplied keys",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {"Cache"})
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
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Map<String, Object>> get(
      @RequestParam(required = false) List<String> keys) {
    Map<String, Object> inMemoryCache = cacheConfigService.getInMemoryCache();

    String cacheId = (String) inMemoryCache.get(Constants.VERSION);
    String cacheVersion = (String) inMemoryCache.get(Constants.CACHE_VERSION);
    ZonedDateTime timestamp = (ZonedDateTime) inMemoryCache.get(Constants.TIMESTAMP);

    HttpHeaders responseHeaders = buildHttpHeaders(cacheId, cacheVersion, timestamp);
    if (keys != null && !keys.isEmpty()) {
      Map<String, Object> returnMap = new HashMap<>();
      keys.forEach(
          k -> {
            if (inMemoryCache.containsKey(k)) {
              returnMap.put(k, inMemoryCache.get(k));
            }
          });
      return ResponseEntity.ok().headers(responseHeaders).body(returnMap);
    } else {
      return ResponseEntity.ok().headers(responseHeaders).body(inMemoryCache);
    }
  }

  @Operation(
      summary = "Get in memory latest full cache id",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {"Cache"})
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
  public ResponseEntity<CacheVersion> id() {
    Map<String, Object> inMemoryCache = cacheConfigService.getInMemoryCache();

    String cacheId = (String) inMemoryCache.get(Constants.VERSION);
    String cacheVersion = (String) inMemoryCache.get(Constants.CACHE_VERSION);
    ZonedDateTime timestamp = (ZonedDateTime) inMemoryCache.get(Constants.TIMESTAMP);

    HttpHeaders responseHeaders = buildHttpHeaders(cacheId, cacheVersion, timestamp);

    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(new CacheVersion((String) inMemoryCache.get(Constants.VERSION)));
  }

  @Operation(
      summary = "Refresh in memory full cache",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {"Cache"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RefreshResponse.class))),
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
  public ResponseEntity<RefreshResponse> refresh() {
    boolean fullCacheInProgress = cacheConfigService.getCacheInProgress();
    if (fullCacheInProgress) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    } else {
      cacheConfigService.doCache();
      Map<String, Object> inMemoryCache = cacheConfigService.getInMemoryCache();

      String cacheId = (String) inMemoryCache.get(Constants.VERSION);
      String cacheVersion = (String) inMemoryCache.get(Constants.CACHE_VERSION);
      ZonedDateTime timestamp = (ZonedDateTime) inMemoryCache.get(Constants.TIMESTAMP);

      HttpHeaders responseHeaders = buildHttpHeaders(cacheId, cacheVersion, timestamp);

      return ResponseEntity.ok()
          .headers(responseHeaders)
          .body(
              RefreshResponse.builder()
                  .version(cacheVersion)
                  .timestamp(timestamp)
                  .id(cacheId)
                  .build());
    }
  }

  private static HttpHeaders buildHttpHeaders(
      String cacheId, String cacheVersion, ZonedDateTime timestamp) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(Constants.HEADER_X_CACHE_ID, cacheId);
    responseHeaders.set(Constants.HEADER_X_CACHE_VERSION, cacheVersion);
    responseHeaders.set(
        Constants.HEADER_X_CACHE_TIMESTAMP, DateTimeFormatter.ISO_DATE_TIME.format(timestamp));
    return responseHeaders;
  }

  @Operation(
      summary = "Get xlsx of latest full cache",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {"Cache"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
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
      value = "/xlsx",
      produces = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
  public ResponseEntity<byte[]> xls() throws IOException {
    byte[] convert = null;
    Map<String, Object> inMemoryCache = cacheConfigService.getInMemoryCache();
    try {
      convert = new JsonToXls(xlsMaskPasswords).convert(inMemoryCache);
    } catch (Exception e) {
      log.error("Error creating xlsx", e);
    }
    String fileName =
        String.format("%s.xlsx", inMemoryCache.getOrDefault(Constants.CACHE_VERSION, "cache"));
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", fileName))
        .body(convert);
  }
}
