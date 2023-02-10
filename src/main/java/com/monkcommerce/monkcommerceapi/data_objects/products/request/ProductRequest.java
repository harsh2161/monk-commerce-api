package com.monkcommerce.monkcommerceapi.data_objects.products.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductRequest
{
    private String categoryId;
    private Integer limit;
    private Integer page;

    public ProductRequest()
    {
        limit = 10;
        page = 1;
    }
}
