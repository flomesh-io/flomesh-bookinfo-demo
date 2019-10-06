package com.redhat.bookinfo.ratings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatingsRepository extends JpaRepository<Rating, UUID> {

    List<Rating> findByProductId(UUID productId);

}
