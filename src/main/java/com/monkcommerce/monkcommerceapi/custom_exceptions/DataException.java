package com.monkcommerce.monkcommerceapi.custom_exceptions;

import org.springframework.stereotype.Component;

@Component
public class DataException extends Exception
{
    public DataException()
    {
        super("Improper Data");
    }
    public DataException(String message)
    {
        super(message);
    }
}
