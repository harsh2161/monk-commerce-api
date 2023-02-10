package com.monkcommerce.monkcommerceapi.data_objects.categories.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesDTO
{
    private Integer page;
    private ArrayList<Category> categories = new ArrayList<>();
    public void AddCategories(ArrayList<Category> addCategories)
    {
        categories.addAll(addCategories);
    }
    public void AddCategories(Category addCategory)
    {
        categories.add(addCategory);
    }
}
