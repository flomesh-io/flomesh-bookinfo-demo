package io.flomesh.bookinfo.ratings;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class RatingsController {

	private RatingsRepository ratingsRepository;

	public RatingsController(RatingsRepository ratingsRepository) {
		this.ratingsRepository = ratingsRepository;
	}
	
	//@GetMapping(value="/ratings/product/{productId}")
	@GetMapping(value="/ratings/{productId}")
	public List<Rating> getRatingsByProduct(@PathVariable UUID productId) {
		return ratingsRepository.findByProductId(productId);
	}

	// curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8101/ratings
	@PostMapping(value="/ratings")
    public Rating createRating(@RequestBody Rating rating) {
		return ratingsRepository.save(rating);
	}
}