package com.monkcommerce.monkcommerceapi.controllers.product;

import com.monkcommerce.monkcommerceapi.business_layer.products.ProductService;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task/products")
@RequiredArgsConstructor
public class ProductController
{
    @Autowired
    private final ProductService productService;
    @GetMapping("/save/categoryId={categoryId}")
    public ResponseEntity<ProcessStatus> getAndStoreProductsFromExternalApi(@PathVariable String categoryId) throws InputException, DataException {
        return ResponseEntity.ok(productService.getAndStoreProductsFromExternalApi(categoryId));
    }
    @PostMapping("")
    public ResponseEntity<ProductDTO> getProduct(@RequestBody ProductRequest request) throws InputException {
        return ResponseEntity.ok(productService.getProducts(request));
    }
}
