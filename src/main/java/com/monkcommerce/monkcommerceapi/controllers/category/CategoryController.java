package com.monkcommerce.monkcommerceapi.controllers.category;

import com.monkcommerce.monkcommerceapi.business_layer.category.CategoriesService;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
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
@RequestMapping("/api/v1/task/categories")
@RequiredArgsConstructor
public class CategoryController
{
    private final LocalBucket bucket = Bucket4j.builder().addLimit(Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)))).build();
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private final CategoriesService categoriesService;
    @GetMapping("/save")
    public ResponseEntity<ProcessStatus> getAndStoreCategoriesFromExternalApi() throws DataException, InterruptedException {
        if(!bucket.tryConsume(5)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        logger.info("Calling third party api started ");
        var response = categoriesService.getAndStoreCategoriesFromExternalApi();
        logger.info("Getting Response of third party api "+response.toString());
        return ResponseEntity.ok(response);
    }
    @PostMapping("")
    public ResponseEntity<CategoriesDTO> getCategories(@RequestBody CategoryRequest request) throws InputException, DataException {
        if(!bucket.tryConsume(1)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        logger.info("Getting categories from our database started");
        var response = categoriesService.getCategories(request);
        logger.info("Fetched categories from our database ended");
        return ResponseEntity.ok(response);
    }
}
