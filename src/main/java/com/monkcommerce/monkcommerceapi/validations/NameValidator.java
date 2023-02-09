package com.monkcommerce.monkcommerceapi.validations;

import java.util.regex.Pattern;

public class NameValidator
{
    private static final String nameRegex = "^[a-zA-Z0-9 ]*$";
    private static final Integer nameLength = 2;
    public static boolean isNameValidBoolean(String name)
    {
        if(name.isBlank() || name.length() < nameLength)
        {
            return false;
        }
        return Pattern.compile(nameRegex)
                .matcher(name)
                .matches();
    }
    public static void isNameValidThrowException(String name)
    {
        if(!isNameValidBoolean(name))
        {
            // throw custom exception
        }
    }
}
