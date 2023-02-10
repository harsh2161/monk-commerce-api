package com.monkcommerce.monkcommerceapi.global;

import com.monkcommerce.monkcommerceapi.constants.ExceptionsType;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.TokenException;
import com.monkcommerce.monkcommerceapi.data_objects.custom_exceptions.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(InputException.class)
    public ResponseEntity<ExceptionDTO> inputException(InputException ex)
    {
        return new ResponseEntity<ExceptionDTO>(new ExceptionDTO(ExceptionsType.IMPROPER_INPUT_EXCEPTION, ""+ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataException.class)
    public ResponseEntity<ExceptionDTO> dataException(DataException ex)
    {
        return new ResponseEntity<ExceptionDTO>(new ExceptionDTO(ExceptionsType.DATA_EXCEPTION,""+ex.getMessage()) ,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ExceptionDTO> tokenException(DataException ex)
    {
        return new ResponseEntity<ExceptionDTO>(new ExceptionDTO(ExceptionsType.TOKEN_EXCEPTION,""+ex.getMessage()),HttpStatus.BAD_REQUEST);
    }
}
