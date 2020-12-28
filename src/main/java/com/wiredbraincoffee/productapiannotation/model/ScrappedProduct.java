package com.wiredbraincoffee.productapiannotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bessam on 28/12/2020
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScrappedProduct {

    private String descriptionLong;
    private String designation;
    private String description;
    private String ean;
    private String largeur;
    private String profondeur;
    private String couleur;

}
