package com.monkcommerce.monkcommerceapi.custom_exceptions;

public class RateLimitException extends Exception {
    public RateLimitException() {
        super();
    }

    public RateLimitException(String message) {
        super(message);
    }
}
