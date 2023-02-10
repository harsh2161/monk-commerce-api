package com.monkcommerce.monkcommerceapi.data_objects.custom_exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionDTO
{
    private String exceptionType;
    private String message;
    public ExceptionDTO(String exceptionType,String message)
    {
        this.exceptionType = exceptionType;
        this.message = message;
    }
}
