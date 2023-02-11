package com.monkcommerce.monkcommerceapi.business_layer.category;

import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.database_layer.category.CategoriesRepository;
import com.monkcommerce.monkcommerceapi.validations.PageAndLimitValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriesService
{
    private static final Logger logger = LoggerFactory.getLogger(CategoriesService.class);
    private final CategoriesRepository categoriesRepository;
    public ProcessStatus getAndStoreCategoriesFromExternalApi() throws DataException {
        return categoriesRepository.getAndStoreCategoriesFromExternalApi();
    }

    public CategoriesDTO getCategories(CategoryRequest request) throws InputException, DataException {
        if(request == null)
            request = new CategoryRequest();

        ValidatePageLimitRequest(request);

        return categoriesRepository.getCategories(request);
    }

    private void ValidatePageLimitRequest(CategoryRequest request) throws InputException {
        PageAndLimitValidator.isPageValidException(request.getPage());
        PageAndLimitValidator.isLimitValidException(request.getLimit());
    }
}
