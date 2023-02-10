package com.monkcommerce.monkcommerceapi.data_objects.categories.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CategoryRequest
{
    private Integer limit;
    private Integer page;

    public CategoryRequest()
    {
        limit = 10;
        page = 1;
    }
}
