package com.redhat.bookinfo.reviews;

import com.redhat.bookinfo.ratings.Rating;
import com.redhat.bookinfo.ratings.RatingsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ReviewsController {

	@Value("${ratings-enabled}")
	private Boolean ratingsEnabled;
	
	private ReviewsRepository reviewRepository;

	private RatingsClient ratingsClient;

	public ReviewsController(ReviewsRepository reviewRepository, RatingsClient ratingsClient) {
		this.reviewRepository = reviewRepository;
		this.ratingsClient = ratingsClient;
	}

	//@GetMapping(value="/reviews/product/{productId}")
	@GetMapping(value="/reviews/{productId}")
    public List<ReviewDto> getReviewsByProductId(@PathVariable UUID productId) {
    	List<ReviewDto> reviewRatings = new ArrayList<>();

    	List<Review> reviews = reviewRepository.findByProductId(productId);
    	List<Rating> ratings = ratingsClient.getRatingsByProduct(productId);

    	for(Review review : reviews) {
    		ReviewDto reviewDto = new ReviewDto(review.getId(), review.getReviewerId(), review.getProductId(), review.getReview(), 0);
    		
    		if(this.ratingsEnabled) {
    			// Find a rating that correlates with the reviewerId
    			for(Rating rating: ratings) {
    				if(rating.getReviewerId().equals(review.getReviewerId())) {
    					reviewDto.setRating(rating.getRating());
    					break; // Only one review allowed per reviewer per product
    				}
    			}
    		}
			reviewRatings.add(reviewDto);
    	}
    	
    	return reviewRatings;
    }

    // curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8102/reviews
    @PostMapping(value="/reviews")
    public ReviewDto createReview(@RequestBody ReviewDto reviewDto) {
		if(this.ratingsEnabled) {
			Rating rating = new Rating(null, reviewDto.getReviewerId(), reviewDto.getProductId(), reviewDto.getRating());
			ResponseEntity<Rating> ratingResponse = ratingsClient.createRating(rating);

			// No way to propagate exception from the fallback, so we need to create it again
			if (ratingResponse.getStatusCode() != HttpStatus.OK) {
				throw new RatingsClient.RatingsUnavailableException();
			}
		}

		Review review = new Review(null, reviewDto.getReviewerId(), reviewDto.getProductId(), reviewDto.getReview());
		review = reviewRepository.save(review);

		reviewDto.setId(review.getId());
		return reviewDto;
    }
}