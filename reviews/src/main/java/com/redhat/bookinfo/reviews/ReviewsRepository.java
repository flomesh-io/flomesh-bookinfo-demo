package com.redhat.bookinfo.reviews;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsRepository extends JpaRepository<Review, UUID> {
	
	List<Review> findByProductId(UUID productId);

}
