package com.wiredbraincoffee.productapiannotation.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author bessam on 27/12/2020
 */

@Data
@AllArgsConstructor
public class ProductEvent {

    private Long eventId;
    private String eventType;
}
