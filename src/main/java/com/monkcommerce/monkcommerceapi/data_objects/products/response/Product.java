package com.monkcommerce.monkcommerceapi.data_objects.products.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
public class Product
{
    private Long sku;
    private String name;
    private double salePrice;
    private ArrayList<ImageURL> images = new ArrayList<>();
    private boolean digital;
    private double shippingCost;
    private String description;
    private Integer customerReviewCount;

    public Product()
    {
        salePrice = 0;
        shippingCost = 0;
        customerReviewCount = 0;
    }
}