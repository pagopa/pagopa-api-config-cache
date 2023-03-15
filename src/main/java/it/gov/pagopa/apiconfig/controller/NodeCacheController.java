package it.gov.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.apiconfig.model.ProblemJson;
import it.gov.pagopa.apiconfig.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.service.ConfigService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/node/cache/schemas")
@Validated
@Slf4j
public class NodeCacheController {

  @Autowired
  private ConfigService configService;

  @Operation(summary = "Get full node v1 config", security = {@SecurityRequirement(name = "ApiKey"),
      @SecurityRequirement(name = "Authorization")}, tags = {"Creditor Institutions",})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigDataV1.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
  @GetMapping(value = "/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigDataV1> cache() throws IOException {
    return ResponseEntity.ok(configService.newCacheV1());
//    return ResponseEntity.ok(new ConfigDataV1());
  }

  @Operation(summary = "Get last node v1 cache version", security = {
      @SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {
      "Creditor Institutions",})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CacheVersion.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
      @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
  @GetMapping(value = "/v1/id", produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CacheVersion> idV1() {
      return ResponseEntity.ok(configService.getCacheV1Id());
  }


}
