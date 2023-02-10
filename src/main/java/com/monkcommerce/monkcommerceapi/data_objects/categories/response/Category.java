package com.monkcommerce.monkcommerceapi.data_objects.categories.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class Category
{
    private String id;
    private String name;
    private Integer noOfProducts;
    public Category()
    {
        noOfProducts = 0;
    }
}
