package com.monkcommerce.monkcommerceapi.controllers.authentication;

import com.monkcommerce.monkcommerceapi.business_layer.authentication.AuthenticationService;
import com.monkcommerce.monkcommerceapi.data_objects.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthenticationRequest;
import com.monkcommerce.monkcommerceapi.data_objects.register.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthenticationController
{
    @Autowired
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@RequestBody RegisterRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthRegisterResponse> authenticate(@RequestBody AuthenticationRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
