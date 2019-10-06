package com.redhat.bookinfo.ratings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private UUID id;
    private UUID reviewerId;
    private UUID productId;
    private int rating;
}