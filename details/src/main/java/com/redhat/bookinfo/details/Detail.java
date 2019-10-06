package com.redhat.bookinfo.details;


/*
        'id' => id,
        'author': book['authors'][0],
        'year': book['publishedDate'],
        'type' => type,
        'pages' => book['pageCount'],
        'publisher' => book['publisher'],
        'language' => language,
        'ISBN-10' => isbn10,
        'ISBN-13' => isbn13
 */

import lombok.Data;

import java.util.UUID;

@Data
public class Detail {
    private UUID id;
    private String title;
    private String description;
    private String author;
    private String printType;
    private int year;
    private int pageCount;
    private String publisher;
    private String language;
    private String isbn;

    public UUID getId() {
        if(id == null)
            id = UUID.nameUUIDFromBytes(isbn.getBytes());
        return id;
    }
}
