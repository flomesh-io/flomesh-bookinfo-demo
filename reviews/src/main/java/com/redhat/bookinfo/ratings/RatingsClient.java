package com.redhat.bookinfo.ratings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client that provides a fallback mechanism when combined with Hystrix.
 */
@FeignClient(
        name = "bookinfo-ratings",
        url = "${bookinfo-ratings.url}",
        fallback = RatingsClient.RatingsClientFallback.class
)
public interface RatingsClient {

    //@GetMapping(value="/ratings/product/{productId}")
    @GetMapping(value="/ratings/{productId}")
    List<Rating> getRatingsByProduct(@PathVariable("productId") UUID productId);

    @PostMapping(value="/ratings")
    ResponseEntity<Rating> createRating(@RequestBody Rating rating);

    @Component
    class RatingsClientFallback implements RatingsClient {

        private Logger LOG = LoggerFactory.getLogger(RatingsClientFallback.class);

        @Override
        public List<Rating> getRatingsByProduct(UUID productId) {
            LOG.info("Fallback for getRatingsByProduct " + productId);
            return new ArrayList<>();
        }

        @Override
        public ResponseEntity<Rating> createRating(Rating rating) {
            LOG.info("Fallback for createRating " + rating);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    class RatingsUnavailableException extends RuntimeException {}
}








