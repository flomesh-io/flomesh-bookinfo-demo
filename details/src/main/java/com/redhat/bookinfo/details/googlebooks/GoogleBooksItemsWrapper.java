package com.redhat.bookinfo.details.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleBooksItemsWrapper {

    private GoogleBooksVolumeInfoWrapper volumeInfo;

}
