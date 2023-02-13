package com.monkcommerce.monkcommerceapi.controllers.product;

import com.monkcommerce.monkcommerceapi.business_layer.products.ProductService;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/task/products")
@RequiredArgsConstructor
public class ProductController
{
    private final LocalBucket bucket = Bucket4j.builder().addLimit(Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)))).build();
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private final ProductService productService;
    @GetMapping("/save/categoryId={categoryId}")
    public ResponseEntity<ProcessStatus> getAndStoreProductsFromExternalApi(@PathVariable String categoryId) throws InputException, DataException, InterruptedException {
        if(!bucket.tryConsume(5)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        logger.info("calling third party api started for products having category id : "+categoryId);
        var response = productService.getAndStoreProductsFromExternalApi(categoryId);
        logger.info("calling third party api ended for products having category id : "+categoryId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("")
    public ResponseEntity<ProductDTO> getProduct(@RequestBody ProductRequest request) throws InputException, DataException {
        if(!bucket.tryConsume(1)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        return ResponseEntity.ok(productService.getProducts(request));
    }
}
