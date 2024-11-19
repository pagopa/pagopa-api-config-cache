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
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
public abstract class StakeholderController {
  protected abstract String[] keys();
  protected abstract String stakeholder();
  protected boolean saveOnDB() {
      return false;
  };

  @Autowired private CacheController cacheController;
  @Autowired private StakeholderConfigService stakeholderConfigService;

  private String X_CACHE_ID = "X-CACHE-ID";
  private String X_CACHE_TIMESTAMP = "X-CACHE-TIMESTAMP";
  private String X_CACHE_VERSION = "X-CACHE-VERSION";

  @Operation(
      summary = "Get selected key of {stakeholder} cache v1 config",
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
  public ResponseEntity<ConfigDataV1> cache(@Deprecated @RequestParam @Parameter(description = "to force the refresh of the cache") Optional<Boolean> refresh) throws IOException {
      if(refresh.orElse(false)) {
          log.warn("Deprecated refresh from stakeholder,change this to call /cache/refresh");
          // TODO set in progress
          cacheController.refresh();
          // TODO remove in progress
      }
      ConfigData config = stakeholderConfigService.getCache(stakeholder(), "v1", keys());

      // save on db according configuration
      if (saveOnDB()) {
          stakeholderConfigService.saveOnDB( config,"v1");
      }

      HttpHeaders responseHeaders = new HttpHeaders();

      responseHeaders.set(X_CACHE_ID, config.getXCacheId());
      responseHeaders.set(X_CACHE_TIMESTAMP, config.getXCacheTimestamp());
      responseHeaders.set(X_CACHE_VERSION, config.getXCacheVersion());

      return ResponseEntity.ok()
              .headers(responseHeaders)
              .body((ConfigDataV1) config.getConfigDataV1());
  }

  @Operation(
      summary = "Get last v1 {stakeholder} cache version",
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
    return ResponseEntity.ok().body(stakeholderConfigService.getVersionId(stakeholder(), "v1"));
  }

    @Operation(
            summary = "Get xlsx of last v1 {stakeholder} cache version",
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
    @GetMapping(value = "/v1/xlsx",
            produces = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public ResponseEntity<byte[]> xls() {
        byte[] convert = null;
        convert = stakeholderConfigService.getXLSX(stakeholder(), "v1");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s_%s_cache.xlsx\"", stakeholder(), "v1"))
                .body(convert);
    }
}
