package it.gov.pagopa.apiconfig.cache.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/cache")
    public ResponseEntity test() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cache/{key}")
    public ResponseEntity getCache(
            @PathVariable("key") String key
    ) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cache/{key}/{value}")
    public ResponseEntity setCache(
            @PathVariable("key") String key,
            @PathVariable("value") String value
    ) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cache/{key}")
    public ResponseEntity deleteCache(
            @PathVariable("key") String key
    ) {
        return ResponseEntity.ok().build();
    }

}
