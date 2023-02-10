package com.monkcommerce.monkcommerceapi.custom_exceptions;

import org.springframework.stereotype.Component;
@Component
public class InputException extends Exception
{
    public InputException()
    {
        super("Improper Inputs");
    }
    public InputException(String message)
    {
        super(message);
    }
}
