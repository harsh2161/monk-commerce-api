package com.monkcommerce.monkcommerceapi.validations;

import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;

import java.util.regex.Pattern;

public class NameValidator
{
    private static final String nameRegex = "^[a-zA-Z0-9 ]*$";
    private static final Integer nameLength = 3;
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
    public static void isNameValidThrowException(String name) throws InputException {
        if(!isNameValidBoolean(name))
        {
            throw new InputException("Invalid Name, minimum 3 length required , name can contain alphabets and numbers only.");
        }
    }
}
