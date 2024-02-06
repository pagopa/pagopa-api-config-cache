package it.gov.pagopa.apiconfig.cache.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.apiconfig.cache.model.ProblemJson;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.util.ConfigDataUtil;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public abstract class CacheController {

  protected abstract String[] keys();

  @Autowired private RefreshController singleController;

  @Operation(
      summary = "Get selected key of cache v1 config",
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
                    schema = @Schema(implementation = ConfigDataV1.class))),
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
      value = "/v1",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigDataV1> cache(@RequestParam @Parameter(description = "to force the refresh of the cache") Optional<Boolean> refresh)
      throws IOException {
      if(refresh.orElse(false)){
          log.warn("Deprecated refresh from nodo,change this to call /cache/refresh");
          singleController.refresh();
      }
      Map<String, Object> inMemoryCache = singleController.getInMemoryCache();
      ConfigDataV1 configDataV1 = ConfigDataUtil.cacheToConfigDataV1(inMemoryCache,keys());

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-CACHE-ID",(String)inMemoryCache.getOrDefault(Constants.version,"n/a"));
      responseHeaders.set("X-CACHE-TIMESTAMP", DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)inMemoryCache.get(Constants.timestamp)));
      responseHeaders.set("X-CACHE-VERSION",(String)inMemoryCache.getOrDefault(Constants.cacheVersion,"n/a"));

      return ResponseEntity.ok()
              .headers(responseHeaders)
              .body(configDataV1);
  }

  @Operation(
      summary = "Get last v1 cache version",
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
            responseCode = "404",
            description = "Not Found",
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
      value = "/v1/id",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CacheVersion> idV1() throws IOException {
    return singleController.id();
  }
}
