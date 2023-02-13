package com.monkcommerce.monkcommerceapi.business_layer.products;

import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import com.monkcommerce.monkcommerceapi.database_layer.products.ProductRepository;
import com.monkcommerce.monkcommerceapi.validations.IdValidator;
import com.monkcommerce.monkcommerceapi.validations.PageAndLimitValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductService
{
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    public ProcessStatus getAndStoreProductsFromExternalApi(String categoryId) throws InputException, DataException, InterruptedException {
        logger.info("started validating inputs "+categoryId);
        IdValidator.isIdValidThrowException(categoryId);
        logger.info("inputs are validated.");
        return productRepository.getAndStoreProductsFromExternalApi(categoryId);
    }

    public ProductDTO getProducts(ProductRequest request) throws InputException, DataException {
        if(request == null)
            request = new ProductRequest();

        ValidatePageLimitRequest(request);
        IdValidator.isIdValidThrowException(request.getCategoryId());

        return productRepository.getProducts(request);
    }

    private void ValidatePageLimitRequest(ProductRequest request) throws InputException {
        PageAndLimitValidator.isPageValidException(request.getPage());
        PageAndLimitValidator.isLimitValidException(request.getLimit());
    }
}
