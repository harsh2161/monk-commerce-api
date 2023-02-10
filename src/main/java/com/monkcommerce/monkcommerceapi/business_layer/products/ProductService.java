package com.monkcommerce.monkcommerceapi.business_layer.products;

import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import com.monkcommerce.monkcommerceapi.database_layer.products.ProductRepository;
import com.monkcommerce.monkcommerceapi.validations.IdValidator;
import com.monkcommerce.monkcommerceapi.validations.PageAndLimitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService
{
    private final ProductRepository productRepository;
    public ProcessStatus getAndStoreProductsFromExternalApi(String categoryId)
    {
        if(!IdValidator.isIdValidBoolean(categoryId))
        {
            // throw custom exception
        }
        return productRepository.getAndStoreProductsFromExternalApi(categoryId);
    }

    public ProductDTO getProducts(ProductRequest request)
    {
        if(request == null)
            request = new ProductRequest();

        if(!ValidatePageLimitRequest(request) && !IdValidator.isIdValidBoolean(request.getCategoryId()))
        {
            // throw custom exception
        }
        return productRepository.getProducts(request);
    }

    private boolean ValidatePageLimitRequest(ProductRequest request)
    {
        return PageAndLimitValidator.isPageValid(request.getPage()) && PageAndLimitValidator.isLimitValid(request.getLimit());
    }
}
