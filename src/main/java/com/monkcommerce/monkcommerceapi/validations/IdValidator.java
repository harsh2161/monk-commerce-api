package com.monkcommerce.monkcommerceapi.validations;

import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;

import java.util.regex.Pattern;

public class IdValidator
{
    private static final String idRegex = "^[a-zA-Z0-9 ]*$";
    private static final Integer idLength = 3;
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
    public static void isIdValidThrowException(String name) throws InputException {
        if(!isIdValidBoolean(name))
        {
            throw new InputException("Id minimum length should be 3 and contains only alphabets and numbers.");
        }
    }
}
