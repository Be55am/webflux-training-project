package com.wiredbraincoffee.productapiannotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author bessam on 26/12/2020
 */

@Document
@Data
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name ;
    private Double price;
}
