package com.monkcommerce.monkcommerceapi.validations;

import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;

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
    public static void isEmailValidThrowException(String email) throws InputException {
        if(!isEmailValidBoolean(email))
        {
            throw new InputException("Improper Email Format");
        }
    }
}
