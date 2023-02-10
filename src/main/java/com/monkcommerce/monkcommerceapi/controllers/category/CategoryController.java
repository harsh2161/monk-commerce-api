package com.monkcommerce.monkcommerceapi.controllers.category;

import com.monkcommerce.monkcommerceapi.business_layer.category.CategoriesService;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task/categories")
@RequiredArgsConstructor
public class CategoryController
{
    @Autowired
    private final CategoriesService categoriesService;
    @GetMapping("/save")
    public ResponseEntity<ProcessStatus> getAndStoreCategoriesFromExternalApi() throws DataException {
        return ResponseEntity.ok(categoriesService.getAndStoreCategoriesFromExternalApi());
    }
    @PostMapping("")
    public ResponseEntity<CategoriesDTO> getCategories(@RequestBody CategoryRequest request) throws InputException {
        return ResponseEntity.ok(categoriesService.getCategories(request));
    }
}
