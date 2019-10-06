package com.redhat.bookinfo.reviews;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
	private UUID id;
    private UUID reviewerId;
    private UUID productId;
    private String review;
    private int rating;
}
