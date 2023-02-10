package com.monkcommerce.monkcommerceapi.business_layer.category;

import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.database_layer.category.CategoriesRepository;
import com.monkcommerce.monkcommerceapi.validations.PageAndLimitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriesService
{
    private final CategoriesRepository categoriesRepository;
    public ProcessStatus getAndStoreCategoriesFromExternalApi()
    {
        return categoriesRepository.getAndStoreCategoriesFromExternalApi();
    }

    public CategoriesDTO getCategories(CategoryRequest request)
    {
        if(request == null)
            request = new CategoryRequest();

        if(!ValidatePageLimitRequest(request))
        {
            // throw custom exception
        }
        return categoriesRepository.getCategories(request);
    }

    private boolean ValidatePageLimitRequest(CategoryRequest request)
    {
        return PageAndLimitValidator.isPageValid(request.getPage()) && PageAndLimitValidator.isLimitValid(request.getLimit());
    }
}
