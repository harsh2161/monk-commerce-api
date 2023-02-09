package com.monkcommerce.monkcommerceapi.validations;

import java.util.regex.Pattern;

public class EmailValidator
{
    private static final String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static boolean isEmailValidBoolean(String email)
    {
        return Pattern.compile(emailRegex)
                .matcher(email)
                .matches();
    }
    public static void isEmailValidThrowException(String email)
    {
        if(!isEmailValidBoolean(email))
        {
            // throw custom exception
        }
    }
}
