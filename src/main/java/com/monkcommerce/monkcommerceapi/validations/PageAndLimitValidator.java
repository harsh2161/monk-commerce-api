package com.monkcommerce.monkcommerceapi.validations;

public class PageAndLimitValidator
{
    public static final Integer PAGE_LIMIT = 100;
    public static boolean isPageValid(Integer page)
    {
        return true;
    }
    public static void isPageValidException(Integer page)
    {
        if(!isPageValid(page))
        {
            // throw Custom Exception
        }
    }
    public static boolean isLimitValid(Integer limit)
    {
        if(limit > PAGE_LIMIT)
            return true;
        return false;
    }
    public static void isLimitValidException(Integer limit)
    {
        if(!isLimitValid(limit))
        {
            // throw Exception
        }
    }
}
