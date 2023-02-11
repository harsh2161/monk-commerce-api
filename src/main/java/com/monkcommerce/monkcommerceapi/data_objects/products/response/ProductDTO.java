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
        try { product.setSku((Long) data.get("sku"));}catch (Exception ex) { return; }
        try { product.setName("" + data.get("name"));}catch (Exception ex) { product.setName("");}
        try { product.setSalePrice(data.get("salePrice"));} catch (Exception ex) { product.setSalePrice(0.0); }
        try { product.setDigital(data.get("digital"));} catch (Exception ex) { product.setDigital(false);}
        try { product.setShippingCost(data.get("shippingCost"));} catch (Exception ex) { product.setShippingCost(0.0); }
        try { product.setDescription(""+data.get("description"));} catch (Exception ex) { product.setDescription(""); }
        try { product.setCustomerReviewCount((Integer) (data.get("customerReviewCount") == null ? 0 : data.get("customerReviewCount")));} catch (Exception ex) { product.setCustomerReviewCount(0); }
        try { product.setImages(data.get("images") == null ? new ArrayList<ImageURL>() : (ArrayList<ImageURL>) data.get("images"));} catch (Exception ex) {product.setImages(new ArrayList<ImageURL>());}
        products.add(product);
    }
}
