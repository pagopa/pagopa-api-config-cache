package it.gov.pagopa.apiconfig.cache.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.apiconfig.cache.model.NodeCacheKey;
import it.gov.pagopa.apiconfig.cache.model.ProblemJson;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
public abstract class CacheController {

  abstract String stakeholder();

  private ConfigDataV1 cfgDataV1 = null;

  @Autowired private ConfigService configService;

  @PostConstruct
  private void loadCacheFromRedis() {
    cfgDataV1 = configService.loadFromRedis(stakeholder());
  }

  @Operation(
      summary = "Get selected key of fdr v1 config",
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
  public ResponseEntity<ConfigDataV1> cache(
      @RequestParam Optional<Boolean> refresh, @RequestParam Optional<NodeCacheKey[]> keys)
      throws IOException {
    boolean cacheV1InProgress = configService.getCacheV1InProgress(stakeholder());
    if (refresh.orElse(false) || cfgDataV1 == null) {
      if (cacheV1InProgress) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
      }
      cfgDataV1 = configService.newCacheV1(stakeholder(), keys);
    }
    return ResponseEntity.ok(cfgDataV1);
  }

  @Operation(
      summary = "Get last fdr v1 cache version",
      security = {@SecurityRequirement(name = "ApiKey")},
      tags = {
        "FdrCache",
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
  public ResponseEntity<CacheVersion> idV1() {
    return ResponseEntity.ok(configService.getCacheV1Id(stakeholder()));
  }
}