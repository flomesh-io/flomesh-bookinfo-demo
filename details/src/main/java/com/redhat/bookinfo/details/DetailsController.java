package com.redhat.bookinfo.details;

import com.redhat.bookinfo.details.googlebooks.GoogleBooksVolumeInfoWrapper;
import com.redhat.bookinfo.details.googlebooks.GoogleBooksWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DetailsController {

    @GetMapping(value="/details/{isbn}")
    public ResponseEntity<Detail> getDetailByIsbn(@PathVariable("isbn") String isbn) {
        //RestTemplate restTemplate = new RestTemplate();
        // TODO: Circuit breaker implementation with FeignClient?
        //ResponseEntity<GoogleBooksWrapper> entity =
        //        restTemplate.getForEntity("https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn, GoogleBooksWrapper.class);

        //if(entity != null && entity.getBody() != null && entity.getBody().getItems() != null && entity.getBody().getItems().length > 0) {
        //    GoogleBooksVolumeInfoWrapper info = entity.getBody().getItems()[0].getVolumeInfo();

            Detail detail = new Detail();
            detail.setIsbn(isbn);
            detail.setTitle("No Name");//info.getTitle());
            detail.setDescription("Bala bala...");//info.getDescription());
            detail.setAuthor("NoName Author");//info.getAuthors()[0]);
            detail.setPrintType("paperback");//info.getPrintType());
            detail.setYear(1595);//Integer.parseInt(info.getPublishedDate()));
            detail.setPageCount(999);//info.getPageCount());
            detail.setPublisher("PublisherA");//info.getPublisher());
            detail.setLanguage("English");//info.getLanguage());

            return new ResponseEntity<>(detail, HttpStatus.OK);
        //}
        //else {
        //    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //}
    }
}
