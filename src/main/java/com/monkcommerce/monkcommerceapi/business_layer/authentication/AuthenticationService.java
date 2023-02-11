package com.monkcommerce.monkcommerceapi.business_layer.authentication;

import com.monkcommerce.monkcommerceapi.business_layer.jwts.JwtService;
import com.monkcommerce.monkcommerceapi.constants.ExceptionsType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    public AuthRegisterResponse register(RegisterRequest request) throws InputException, ExecutionException, InterruptedException, DataException
    {
        logger.info("Register Process of Service Layer "+request.getEmail()+" is started.");

        ValidateRegisterRequest(request);

        request = createUserForRegister(request);

        if(!authenticationRepository.registerUser(request))
        {
            logger.error("Unable to register User "+request.getEmail());
            throw new DataException("User Not Registered, Try Again :(");
        }

        var jwtToken = jwtService.generateToken(request.getEmail());

        logger.info("Register Process of Service Layer "+request.getEmail()+" with token : "+ jwtToken +"is ended.");
        return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
    }

    public AuthRegisterResponse authenticate(AuthenticationRequest request) throws ExecutionException, InterruptedException, InputException, DataException {
        logger.info("Authenticate Process of Service Layer "+request.getEmail()+" is started.");
        AuthenticateRegisterRequest(request);

        var response = authenticationRepository.authenticateUser(request);

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), response.getPassword());

        if(!isPasswordMatch)
        {
            logger.error("Invalid Password written by user : "+request.getEmail());
            throw new InputException("Invalid Password!!");
        }

        var jwtToken = jwtService.generateToken(request.getEmail());
        logger.info("Authenticate Process of Service Layer "+request.getEmail()+" with token : "+ jwtToken +"is ended.");
        return AuthRegisterResponse.builder().token(jwtToken).id(null).email(null).password(null).build();
    }

    private void ValidateRegisterRequest(RegisterRequest request) throws InputException
    {
        logger.info("Validation Process of Service Layer "+request.getEmail()+" is started.");
        if(request.getName().isBlank() || request.getEmail().isBlank() || request.getPassword().isBlank())
        {
            logger.error("Got Exception while validating Exception Type : "+ ExceptionsType.IMPROPER_INPUT_EXCEPTION + " with message Name, Email, Password All Are Mandatory.");
            throw new InputException("Name, Email, Password All Are Mandatory.");
        }

        NameValidator.isNameValidThrowException(request.getName());

        EmailValidator.isEmailValidThrowException(request.getEmail());

        PasswordValidator.isPasswordValidThrowException(request.getPassword());
        logger.info("Inputs are properly validated");
    }
    private RegisterRequest createUserForRegister(RegisterRequest registerUser)
    {
        registerUser.setId(JwtS.User + UUID.randomUUID());
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        logger.info("Assigned UID and encoding passwords of "+registerUser.getEmail());
        return registerUser;
    }

    private void AuthenticateRegisterRequest(AuthenticationRequest request) throws InputException
    {
        logger.info("Validation Process of Service Layer "+request.getEmail()+" is started.");
        if (request.getEmail().isBlank() || request.getPassword().isBlank())
        {
            logger.error("Got Exception while validating Exception Type : "+ ExceptionsType.IMPROPER_INPUT_EXCEPTION + " with message Email, Password All Are Mandatory.");
            throw new InputException("Email, Password All Are Mandatory.");
        }

        EmailValidator.isEmailValidThrowException(request.getEmail());

        PasswordValidator.isPasswordValidThrowException(request.getPassword());
        logger.info("Inputs are properly validated");
    }
}
