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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/fdr/cache/schemas")
@Validated
@Slf4j
public class FdrCacheController extends CacheController{
  @Override
  String stakeholder() {
    return "fdr";
  }
}
