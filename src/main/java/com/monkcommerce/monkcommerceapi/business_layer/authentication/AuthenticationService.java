package com.monkcommerce.monkcommerceapi.business_layer.authentication;

import com.monkcommerce.monkcommerceapi.business_layer.jwts.JwtService;
import com.monkcommerce.monkcommerceapi.constants.JwtS;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.InputException;
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
    public AuthRegisterResponse register(RegisterRequest request) throws InputException, ExecutionException, InterruptedException, DataException {
        ValidateRegisterRequest(request);

        request = createUserForRegister(request);

        if(!authenticationRepository.registerUser(request))
        {
            throw new DataException("User Not Registered, Try Again :(");
        }

        var jwtToken = jwtService.generateToken(request.getEmail());

        return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
    }

    public AuthRegisterResponse authenticate(AuthenticationRequest request) throws ExecutionException, InterruptedException, InputException, DataException {
        AuthenticateRegisterRequest(request);

        var response = authenticationRepository.authenticateUser(request);

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), response.getPassword());

        if(!isPasswordMatch)
        {
            throw new InputException("Invalid Password!!");
        }

        var jwtToken = jwtService.generateToken(request.getEmail());
        return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
    }

    private void ValidateRegisterRequest(RegisterRequest request) throws InputException {

        if(request.getName().isBlank() || request.getEmail().isBlank() || request.getPassword().isBlank())
        {
            throw new InputException("Name, Email, Password All Are Mandatory.");
        }

        NameValidator.isNameValidThrowException(request.getName());

        EmailValidator.isEmailValidThrowException(request.getEmail());

        PasswordValidator.isPasswordValidThrowException(request.getPassword());
    }
    private RegisterRequest createUserForRegister(RegisterRequest registerUser)
    {
        registerUser.setId(JwtS.User + UUID.randomUUID());
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        return registerUser;
    }

    private void AuthenticateRegisterRequest(AuthenticationRequest request) throws InputException
    {
        if (request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new InputException("Email, Password All Are Mandatory.");
        }

        EmailValidator.isEmailValidThrowException(request.getEmail());

        PasswordValidator.isPasswordValidThrowException(request.getPassword());
    }
}
