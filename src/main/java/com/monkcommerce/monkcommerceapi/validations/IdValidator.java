package com.monkcommerce.monkcommerceapi.validations;

import java.util.regex.Pattern;

public class IdValidator
{
    private static final String idRegex = "^[a-zA-Z0-9 ]*$";
    private static final Integer idLength = 2;
    public static boolean isIdValidBoolean(String name)
    {
        if(name.isBlank() || name.length() < idLength)
        {
            return false;
        }
        return Pattern.compile(idRegex)
                .matcher(name)
                .matches();
    }
    public static void isIdValidThrowException(String name)
    {
        if(!isIdValidBoolean(name))
        {
            // throw custom exception
        }
    }
}
