package com.redhat.bookinfo.reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Entity;

@TypeDefs( { @TypeDef( name = "uuid-pgh2", typeClass = PostgresH2UUIDType.class, defaultForType = UUID.class ) } )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
	@Id
    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
	private UUID id;
    private UUID reviewerId;
    private UUID productId;
    private String review;
}