package com.monkcommerce.monkcommerceapi.data_objects.products.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO
{
    private Integer page;
    private ArrayList<Product> products = new ArrayList<>();
    public void AddCategories(ArrayList<Product> addCategories)
    {
        products.addAll(addCategories);
    }
    public void AddCategories(Product addCategory)
    {
        products.add(addCategory);
    }

    public void AddCategories(Map<String, Object> data)
    {
        if(data == null)
            return;
        Product product = new Product();
        product.setSku((Long) data.get("sku"));
        product.setName("" + data.get("name"));
        product.setSalePrice((double) data.get("salePrice"));
        product.setDigital((boolean) data.get("digital"));
        product.setShippingCost((double) data.get("shippingCost"));
        product.setDescription(""+data.get("description"));
        product.setCustomerReviewCount((Integer) (data.get("customerReviewCount") == null ? 0 : data.get("customerReviewCount")));
        product.setImages(data.get("images") == null ? new ArrayList<ImageURL>() : (ArrayList<ImageURL>) data.get("images"));
        products.add(product);
    }
}
