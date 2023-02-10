package com.monkcommerce.monkcommerceapi.data_objects.products.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ImageURL
{
    private String href;
}
