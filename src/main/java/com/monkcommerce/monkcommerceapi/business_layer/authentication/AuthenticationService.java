package com.monkcommerce.monkcommerceapi.business_layer.authentication;

import com.monkcommerce.monkcommerceapi.business_layer.jwts.JwtService;
import com.monkcommerce.monkcommerceapi.constants.JwtS;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthenticationRequest;
import com.monkcommerce.monkcommerceapi.data_objects.register.RegisterRequest;
import com.monkcommerce.monkcommerceapi.database_layer.authentication.AuthenticationRepository;
import com.monkcommerce.monkcommerceapi.validations.EmailValidator;
import com.monkcommerce.monkcommerceapi.validations.NameValidator;
import com.monkcommerce.monkcommerceapi.validations.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthenticationService
{
    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthRegisterResponse register(RegisterRequest request) throws ExecutionException, InterruptedException
    {
        if(!ValidateRegisterRequest(request))
        {
            // throw custom exception
            return new AuthRegisterResponse();
        }

        request = createUserForRegister(request);

        if(!authenticationRepository.registerUser(request))
        {
            // throw custom exception
        }

        var jwtToken = jwtService.generateToken(request.getEmail());

        return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
    }

    public AuthRegisterResponse authenticate(AuthenticationRequest request) throws ExecutionException, InterruptedException
    {
        if(!AuthenticateRegisterRequest(request))
        {
            // throw custom exception
            return new AuthRegisterResponse();
        }

        var response = authenticationRepository.authenticateUser(request);

        if(response == null)
        {
            // throw custom exception
        }

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), response.getPassword());

        if(isPasswordMatch)
        {
            var jwtToken = jwtService.generateToken(request.getEmail());
            return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
        }

        // throw custom exception
        return new AuthRegisterResponse();
    }

    private boolean ValidateRegisterRequest(RegisterRequest request)
    {
        if(request.getName().isBlank() || request.getEmail().isBlank() || request.getPassword().isBlank())
        {
            return false;
        }

        if(!NameValidator.isNameValidBoolean(request.getName()))
        {
            return false;
        }

        if(!EmailValidator.isEmailValidBoolean(request.getEmail()))
        {
            return false;
        }

        if(!PasswordValidator.isPasswordValidBoolean(request.getPassword()))
        {
            return false;
        }

        return true;
    }
    private RegisterRequest createUserForRegister(RegisterRequest registerUser)
    {
        registerUser.setId(JwtS.User + UUID.randomUUID());
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        return registerUser;
    }

    private boolean AuthenticateRegisterRequest(AuthenticationRequest request)
    {
        if(request.getEmail().isBlank() || request.getPassword().isBlank())
        {
            return false;
        }

        if(!EmailValidator.isEmailValidBoolean(request.getEmail()))
        {
            return false;
        }

        if(!PasswordValidator.isPasswordValidBoolean(request.getPassword()))
        {
            return false;
        }

        return true;
    }
}
