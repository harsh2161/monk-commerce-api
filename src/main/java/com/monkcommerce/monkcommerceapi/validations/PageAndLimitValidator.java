package com.monkcommerce.monkcommerceapi.validations;

import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;

public class PageAndLimitValidator
{
    public static final Integer PAGE_LIMIT = 100;
    public static boolean isPageValid(Integer page)
    {
        return true;
    }
    public static void isPageValidException(Integer page) throws InputException {
        if(!isPageValid(page))
        {
            throw new InputException("Page Should have Proper Value");
        }
    }
    public static boolean isLimitValid(Integer limit)
    {
        return limit < PAGE_LIMIT;
    }
    public static void isLimitValidException(Integer limit) throws InputException {
        if(!isLimitValid(limit))
        {
            throw new InputException("Limit is max 100 only");
        }
    }
}
