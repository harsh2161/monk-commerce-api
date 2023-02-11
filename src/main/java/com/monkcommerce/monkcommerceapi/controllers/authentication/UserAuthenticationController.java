package com.monkcommerce.monkcommerceapi.controllers.authentication;

import com.monkcommerce.monkcommerceapi.business_layer.authentication.AuthenticationService;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthenticationRequest;
import com.monkcommerce.monkcommerceapi.data_objects.register.RegisterRequest;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthenticationController
{
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);
    private final LocalBucket bucket = Bucket4j.builder().addLimit(Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)))).build();
    @Autowired
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@RequestBody RegisterRequest request) throws InputException, ExecutionException, InterruptedException, DataException
    {
        if(!bucket.tryConsume(1)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        logger.info("Register Process of "+request.getEmail()+" is started.");
        var response = service.register(request);
        logger.info("Getting Response of registering "+request.getEmail()+" is :"+response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthRegisterResponse> authenticate(@RequestBody AuthenticationRequest request) throws ExecutionException, InterruptedException, InputException, DataException
    {
        if(!bucket.tryConsume(1)) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        logger.info("Authentication Process of "+request.getEmail()+" is started.");
        var response = service.authenticate(request);
        logger.info("Getting Response of authentication "+request.getEmail()+" is :"+response);
        return ResponseEntity.ok(response);
    }
}
