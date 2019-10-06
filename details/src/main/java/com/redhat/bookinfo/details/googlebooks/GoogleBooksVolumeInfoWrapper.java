package com.redhat.bookinfo.details.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleBooksVolumeInfoWrapper {

    private String title;
    private String publisher;
    private String publishedDate;
    private String[] authors;
    private String description;
    private int pageCount;
    private String printType;
    private String[] categories;
    private Map<String, String> imageLinks = new HashMap<String, String>();
    private String language;

}
