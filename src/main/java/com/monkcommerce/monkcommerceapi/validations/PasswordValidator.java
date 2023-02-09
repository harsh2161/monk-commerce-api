package com.monkcommerce.monkcommerceapi.validations;

import java.util.regex.Pattern;

public class PasswordValidator
{
    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
    public static boolean isPasswordValidBoolean(String password)
    {
        return Pattern.compile(passwordRegex)
                .matcher(password)
                .matches();
    }
    public static void isPasswordValidThrowException(String password)
    {
        if(!isPasswordValidBoolean(password))
        {
            // throw custom exception
        }
    }
}
