package com.monkcommerce.monkcommerceapi.validations;

import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;

import java.util.regex.Pattern;

public class PasswordValidator
{
    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
    public static boolean isPasswordValidBoolean(String password)
    {
        return Pattern.compile(passwordRegex)
                .matcher(password)
                .matches() && password.length() > 8 && password.length() < 15;
    }
    public static void isPasswordValidThrowException(String password) throws InputException {
        if(!isPasswordValidBoolean(password))
        {
            throw new InputException("Password Contains minimum 1 lower case letter, 1 upper case letter, special character and one number and 8 to 15 characters");
        }
    }
}
