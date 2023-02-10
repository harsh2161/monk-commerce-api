package com.monkcommerce.monkcommerceapi.custom_exceptions;

import org.springframework.stereotype.Component;

@Component
public class TokenException extends Exception
{
    public TokenException()
    {
        super("InValid Token");
    }
    public TokenException(String message)
    {
        super(message);
    }
}
